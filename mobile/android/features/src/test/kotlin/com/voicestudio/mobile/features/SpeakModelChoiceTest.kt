package com.voicestudio.mobile.features

import com.voicestudio.mobile.features.speak.chooseSpeakModel
import com.voicestudio.mobile.features.speak.speakModelKey
import com.voicestudio.mobile.features.speak.speakLanguage
import com.voicestudio.mobile.local.ModelLicence
import com.voicestudio.mobile.local.PhoneModel
import org.junit.Assert.*
import org.junit.Test

class SpeakModelChoiceTest {
    private fun model(id: String,capability: String,languages: List<String> = listOf("en"))=PhoneModel(id,capability,"sherpa-onnx","org/m","a".repeat(40),emptyList(),1,languages,1.0,ModelLicence("mit","commercial-ok",false,"","","b".repeat(64)))
    private val models=listOf(model("tts-a","tts"),model("tts-b","tts"),model("arabic-tts","tts",listOf("en","ar")),model("clone-a","clone"),model("clone-b","clone"))
    @Test fun presetAndCloneChoicesDoNotOverwriteEachOther() {
        var choices=emptyMap<String,String>()
        choices=choices+(speakModelKey("tts") to "tts-b")
        choices=choices+(speakModelKey("clone") to "clone-b")
        assertEquals("tts-b",chooseSpeakModel(models,setOf("tts-a"),choices,"tts")?.id)
        assertEquals("clone-b",chooseSpeakModel(models,emptySet(),choices,"clone")?.id)
    }
    @Test fun explicitReferenceOnlyChoiceStaysVisibleBeforeVoiceSelection() {
        assertEquals("clone-b",chooseSpeakModel(models,setOf("tts-a"),mapOf("speak" to "clone-b"),"tts")?.id)
        assertEquals("arabic-tts",chooseSpeakModel(models,setOf("clone-b"),emptyMap(),"tts",preferredLanguage="ar")?.id)
    }
    @Test fun automaticChoicePrefersFittingInstalledModelAndExplicitOverrideRemainsAvailable() {
        val heavy=model("heavy","clone").copy(minRamGb=8.0)
        val small=model("small","clone").copy(minRamGb=3.0)
        val candidates=listOf(heavy,small);val installed=candidates.map {it.id}.toSet()
        assertEquals("small",chooseSpeakModel(candidates,installed,emptyMap(),"clone",7_512_518_656)?.id)
        assertEquals("heavy",chooseSpeakModel(candidates,installed,mapOf("speak-clone" to "heavy"),"clone",7_512_518_656)?.id)
    }
    @Test fun withoutAChoiceFirstInstalledThenFirstModelOfThatCapability() {
        assertEquals("clone-b",chooseSpeakModel(models,setOf("clone-b"),emptyMap(),"clone")?.id)
        assertEquals("tts-a",chooseSpeakModel(models,emptySet(),emptyMap(),"tts")?.id)
    }
    @Test fun freshPresetSpeechPrefersArabicCapableTtsModelWhenArabicIsPreferred() {
        assertEquals("arabic-tts",chooseSpeakModel(models,emptySet(),emptyMap(),"tts",preferredLanguage="ar")?.id)
    }
    @Test fun englishIsTheDefaultSpeechLanguage() {
        assertEquals("en",speakLanguage("en",false,listOf("ar","en"),false))
        assertEquals("ar",speakLanguage("ar",true,listOf("ar","en"),false))
    }
    @Test fun explicitPresetModelChoiceOverridesArabicDefault() {
        assertEquals("tts-b",chooseSpeakModel(models,emptySet(),mapOf("speak" to "tts-b"),"tts")?.id)
    }
    @Test fun installedArabicModelBeatsOneThatNeedsADownload() {
        val first=model("arabic-a","tts",listOf("ar"));val second=model("arabic-b","tts",listOf("ar"))
        assertEquals("arabic-b",chooseSpeakModel(listOf(first,second),setOf("arabic-b"),emptyMap(),"tts",preferredLanguage="ar")?.id)
    }
    @Test fun arabicIsDefaultUntilUserPicksAnotherLanguage() {
        assertEquals("ar",speakLanguage("ar",false,listOf("en","ar"),false,preferred="ar"))
        assertEquals("en",speakLanguage("en",true,listOf("en","ar"),false,preferred="ar"))
        assertEquals("ar",speakLanguage("fr",true,listOf("en","ar"),false,preferred="ar"))
        assertEquals("en",speakLanguage("ar",false,listOf("en"),false,preferred="ar"))
        assertEquals("auto",speakLanguage("ar",true,listOf("en","ar"),true,preferred="ar"))
    }
    @Test fun arabicModelIsNotPreferredWhenItExceedsAvailableRam() {
        val arabic=model("arabic-heavy","tts",listOf("en","ar")).copy(minRamGb=10.0)
        val english=model("english-small","tts").copy(minRamGb=2.0)
        assertEquals("english-small",chooseSpeakModel(listOf(english,arabic),emptySet(),emptyMap(),"tts",8_000_000_000,preferredLanguage="ar")?.id)
    }
}


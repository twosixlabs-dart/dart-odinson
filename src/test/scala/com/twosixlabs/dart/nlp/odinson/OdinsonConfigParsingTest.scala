package com.twosixlabs.dart.nlp.odinson

import better.files.File

class OdinsonConfigParsingTest extends OdinsonTestBase {

    "Odinson Config Parsing" should "just work™" in {
        ODINSON_CONF.annotator.processor shouldBe "fast_nlp"
        ODINSON_CONF.index.dir shouldBe File( "target/test_index" )
        ODINSON_CONF.fields.rawTokenField shouldBe "raw"
        ODINSON_CONF.fields.addToNormalizedField shouldBe Seq( "raw", "word" )
        ODINSON_CONF.compiler.aggressiveNormalization shouldBe true
        ODINSON_CONF.compiler.incomingTokenField shouldBe ODINSON_CONF.fields.incomingTokenField
        ODINSON_CONF.extractor.rulesHome shouldBe File( "src/test/resources/rules" )
    }

}

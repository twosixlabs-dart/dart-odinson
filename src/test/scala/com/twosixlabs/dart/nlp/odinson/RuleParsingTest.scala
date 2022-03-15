package com.twosixlabs.dart.nlp.odinson

import ai.lum.odinson.compiler.QueryCompiler
import ai.lum.odinson.digraph.Vocabulary
import ai.lum.odinson.{Extractor, RuleReader}
import better.files.File
import com.twosixlabs.dart.nlp.odinson.RuleYamlFormat._

class RuleParsingTest extends OdinsonTestBase {

    private val ruleReader : RuleReader = new RuleReader( QueryCompiler( ODINSON_CONF.configShim(), Vocabulary.empty ) )

    "Rule YAML parser" should "parse a YAML string into a rule resource" in {
        val yaml = File( "src/test/resources/rules/examples/get-nouns.yml" ).contentAsString

        val value = fromRuleYaml( yaml )

        value.rules.size shouldBe 1
        value.rules.head.name shouldBe "match-all-nouns"
        value.rules.head.ruleType shouldBe "basic"
        value.rules.head.pattern.stripLineEnd shouldBe "(?<noun>[tag=/N.*/])"
    }

    "Rule YAML parser" should "convert a Rule Resource into a valid Odinson YAML file" in {
        val rule = Rule( name = "testrule",
                         ruleType = "basic",
                         label = "Test",
                         pattern = "(?<source>[lemma=Africa]) <nsubj (?<action> [lemma=/.*/] [tag=/V.*/]) >dobj (?<target>[tag=/N.*/])" )

        val resource = RuleResource( Seq( rule ) )
        val rules = toRuleYaml( resource )

        val extractors : Seq[ Extractor ] = ruleReader.compileRuleString( rules )
        extractors.size shouldBe 1
    }

    "Rule YAML Parsing" should "consolidate an entire ruleset" in {
        val ruleHome = File( "src/test/resources/rules" )

        val result = RuleResource.loadRuleset( ruleHome, "examples" )
        result.rules.size shouldBe 2

        val rules = toRuleYaml( result )

        val extractors : Seq[ Extractor ] = ruleReader.compileRuleString( rules )
        extractors.size shouldBe 2
    }

}

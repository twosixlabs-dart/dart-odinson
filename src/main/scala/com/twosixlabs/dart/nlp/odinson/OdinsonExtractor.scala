package com.twosixlabs.dart.nlp.odinson

import ai.lum.odinson.utils.QueryUtils
import ai.lum.odinson.{Extractor, ExtractorEngine, Mention}
import better.files.File
import com.twosixlabs.dart.nlp.odinson.RuleYamlFormat._
import com.twosixlabs.dart.nlp.odinson.config.OdinsonConfig


sealed trait QueryType

case object LEMMA extends QueryType

case object NORM extends QueryType

case object LITERAL extends QueryType

case class ExtractorQuery( ruleset : String, filename : Option[ String ] = None, variables : Map[ String, String ] = Map(), queryType : QueryType = LEMMA )


class OdinsonExtractor( odinsonConfig : OdinsonConfig ) {

    private lazy val engine : ExtractorEngine = ExtractorEngine.fromConfig( odinsonConfig.configShim() )
    private val rulesHome : File = odinsonConfig.extractor.rulesHome

    def execute( query : ExtractorQuery ) : Seq[ Mention ] = {
        val extractors = buildExtractors( query )
        engine.extractNoState( extractors ).toSeq
    }

    private def buildExtractors( query : ExtractorQuery ) : Seq[ Extractor ] = {
        val varPatterns = query.queryType match {
            case LEMMA => query.variables.map( v => (v._1, toPattern( "lemma", v._2 )) )
            case NORM => query.variables.map( v => (v._1, toPattern( "norm", v._2 )) )
            case LITERAL => query.variables
        }
        val rules = query.filename match {
            case Some( ruleFile ) => toRuleYaml( RuleResource.loadRuleFile( File( s"${rulesHome}/${query.ruleset}/${ruleFile}" ) ) )
            case None => toRuleYaml( RuleResource.loadRuleset( rulesHome, query.ruleset ) )
        }
        engine.compileRuleString( rules, varPatterns )
    }

    private def toPattern( field : String, value : String ) : String = {
        s"[${field}=${QueryUtils.maybeQuoteLabel( value )}]"
    }

}

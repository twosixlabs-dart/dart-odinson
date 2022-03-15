package com.twosixlabs.dart.nlp.odinson

import better.files.File
import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

case class Rule( @BeanProperty @JsonProperty( "name" ) name : String,
                 @BeanProperty @JsonProperty( "type" ) ruleType : String,
                 @BeanProperty @JsonProperty( "label" ) label : String,
                 @BeanProperty @JsonProperty( "pattern" ) pattern : String ) {
}

case class RuleResource( @BeanProperty @JsonProperty( "rules" ) rules : Seq[ Rule ] )


object RuleResource {

    import com.twosixlabs.dart.nlp.odinson.RuleYamlFormat._

    def loadRuleset( dir : File, ruleset : String ) : RuleResource = {
        val consolidated = ( dir / ruleset )
          .glob( "*.{yml,yaml}" )
          .map( file => fromRuleYaml( file.contentAsString ) )
          .foldRight( List[ Rule ]() )( ( resource, rules ) => rules ++ resource.rules )

        RuleResource( consolidated )
    }

    def loadRuleset( rules : String ) : RuleResource = {
        fromRuleYaml( rules )
    }

    def loadRuleFile( file : File ) : RuleResource = {
        fromRuleYaml( file.contentAsString )
    }

}

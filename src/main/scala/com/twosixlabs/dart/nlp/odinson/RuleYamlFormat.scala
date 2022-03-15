package com.twosixlabs.dart.nlp.odinson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature._
import com.fasterxml.jackson.dataformat.yaml.{YAMLFactory, YAMLFactoryBuilder}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object RuleYamlFormat {

    private val mapper : ObjectMapper = {
        val yamlConfig =
            new YAMLFactoryBuilder( new YAMLFactory )
              .configure( SPLIT_LINES, false )
              .build()

        val m = new ObjectMapper( yamlConfig )
        m.registerModule( DefaultScalaModule )
    }

    def toRuleYaml( resource : RuleResource ) : String = {
        mapper.writeValueAsString( resource ).stripLineEnd
    }

    def fromRuleYaml( yaml : String ) : RuleResource = {
        mapper.readValue[ RuleResource ]( yaml.getBytes, classOf[ RuleResource ] )
    }
}

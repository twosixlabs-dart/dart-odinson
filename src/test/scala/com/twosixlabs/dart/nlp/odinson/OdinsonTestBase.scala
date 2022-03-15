package com.twosixlabs.dart.nlp.odinson

import ai.lum.odinson.{Document => OdinsonDocument}
import better.files.File
import com.twosixlabs.dart.nlp.odinson.config.OdinsonConfig
import com.twosixlabs.dart.nlp.taxero.TaxeroConfig
import com.twosixlabs.dart.test.base.StandardTestBase3x
import com.typesafe.config.ConfigFactory

trait OdinsonTestBase extends StandardTestBase3x {

    val ODINSON_CONF : OdinsonConfig = OdinsonConfig.fromConfig( ConfigFactory.load( "odinson.conf" ).getConfig( "odinson" ) )

    val ODINSON_DOCS : Seq[ OdinsonDocument ] =
        File( "src/test/resources/docs" )
          .list( _.extension.getOrElse( "" ) == ( ".json" ) )
          .toList
          .map( file => OdinsonDocument.fromJson( file.contentAsString ) )


    val TAXERO_CONFIG : TaxeroConfig = TaxeroConfig.fromConfig( ConfigFactory.load( "taxero.conf" ) )
}

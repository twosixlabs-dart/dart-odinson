package com.twosixlabs.dart.nlp.odinson

import org.clulab.dynet.Utils.initializeDyNet
import org.slf4j.{Logger, LoggerFactory}

object Odinson {
    private lazy val LOG : Logger = LoggerFactory.getLogger( getClass )

    def initDynet( autoBatch : Boolean = false, dynetMem : String = "" ) : Unit = initializeDyNet( autoBatch, dynetMem )

}

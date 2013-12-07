package net.ellisw.quvault.server.scope

import scala.collection.mutable.ArrayBuffer
import scala.xml.Node
import scala.xml.NodeSeq


sealed class QuRenderResult {
	var xhtml: NodeSeq = _
	val fields = new ArrayBuffer[QuFieldData]
}

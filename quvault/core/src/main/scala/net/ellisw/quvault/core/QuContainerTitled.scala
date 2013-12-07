package net.ellisw.quvault.core;

class QuContainerTitled(parent: QuObject) extends QuContainer(parent) {
  var title: Option[String] = None
  var keywords: Option[String] = None

  enumerated = true
}

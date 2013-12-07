package net.ellisw.quvault.core

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.Map
import scala.collection.mutable.Set

class QuResource(
  val name: String,
  val kind: String,
  val content: String
)


class QuObject(val parent: QuObject) {
  protected var inheritParentParams = true
  protected var inheritParentVaris = true
  protected var inheritParentResources = true
  protected var enumerated = false
  private val params = Map[String, String]();
  private val varis = new LinkedHashMap[String, QuVari]();
  private val resources = Map[String, QuResource]();
  
  def getParam(name: String): Option[String] = {
    val param = {
      if (params.contains(name))
        params.get(name)
      else if (parent != null && inheritParentParams)
        parent.getParam(name)
      else
        None
    }
    
    if (param.isDefined && param.get != null)
      param
    else
      None
  }
  
  def getVari(name: String): Option[QuVari] = {
    val vari =
      if (varis.contains(name))
        varis.get(name)
      else if (parent != null && inheritParentVaris)
        parent.getVari(name)
      else
        None
    
    if (vari.isDefined && vari.get != null)
      vari
    else
      None
  }

  def getResource(name: String): Option[QuResource] = {
    val resource =
      if (resources.contains(name))
        resources.get(name)
      else if (parent != null && inheritParentResources)
        parent.getResource(name)
      else
        None
    
    if (resource.isDefined && resource.get != null)
      resource
    else
      None
  }
  
  def addParams(params: Map[String, String]) {
    this.params ++= params
  }
  
  def setParam(name: String, value: String) {
    params. += (name -> value)
  }
  
  def setVari(name: String) {
    varis += (name -> new QuVari(name))
  }
  
  def setVari(name: String, nameHtml: String, description: String) {
    varis += (name -> new QuVari(name, nameHtml, Some(description)))
  }
  
  def setResource(name: String, kind: String, content: String) {
    resources += (name -> new QuResource(name, kind, content))
  }
  
  def fillParamMap: Map[String, String] = {
    val map = Map[String, String]()
    fillParamMap(map);
    map
  }
  
  def fillParamMap(map: Map[String, String]) {
    if (parent != null && inheritParentParams)
      parent.fillParamMap(map)
    map ++= params
  }
  
  def fillVariMap(map: Map[String, QuVari]) {
    if (parent != null && inheritParentVaris)
      parent.fillVariMap(map)
    map ++= varis
  }
  
  /*def fillVariSet(vset: Set[QuVari]) {
    if (parent != null && inheritParentVaris)
      parent.fillVariSet(vset)
    vset ++= varis
  }*/
}

package net.ellisw.quvault.lift.api

import scala.xml._ 

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.http.rest.XMLApiHelper

import net.ellisw.quvault.vault.ProbServer


object VaultApi extends XMLApiHelper {
  def dispatch: LiftRules.DispatchPF = {
    case Req(List("api-vault", "standard", x @ _*), "", GetRequest) => () => handleStandard(x)
    // case Req(List("api", _), "", _) => failure _
  }
  
  def handleStandard(x: Seq[String]): LiftResponse = {
    println("S.hostAndPath: "+ S.hostAndPath)
    val sPath = x.toArray.mkString("/")
    val args = Array(sPath)
    val sResponse = ProbServer.getStringResponse(args)
    val elemResponse = XML.loadString(sResponse)
    val e: Box[NodeSeq] = Full(elemResponse)
    e
  }
  
  def createTag(in: NodeSeq) = (<quvault_api>{in}</quvault_api>)

}

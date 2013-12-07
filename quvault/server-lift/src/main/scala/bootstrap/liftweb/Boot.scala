package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._

import scala.xml._ 
import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.http.rest.XMLApiHelper

import net.ellisw.quvault.lift.api._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("net.ellisw.quvault.lift")

    // Build SiteMap
    val menus =
      Menu(Loc("home", List("index"), "Home")) ::
      Menu(Loc("question_list", List("question_list"), "Questions")) ::
      Menu(Loc("view", List("view") -> true, "View")) ::
      Menu(Loc("quView", List("quView") -> true, "QuView")) ::
      Menu(Loc("help_mathlib", List("help_mathlib") -> true, "Help:Mathlib")) ::
      Nil
    LiftRules.setSiteMap(SiteMap(menus : _*))
    
    LiftRules.dispatch.prepend(VaultApi.dispatch)
    
    LiftRules.rewrite.append {
      case RewriteRequest(ParsePath(List("view", sSource, sFolder, sObject, sAction), _, _, _), _, _) =>
        RewriteResponse("quView" :: Nil, Map("_source" -> sSource, "_folder" -> sFolder, "_object" -> sObject, "_action" -> sAction))
      case RewriteRequest(ParsePath(List("view", sSource, sFolder, sObject), _, _, _), _, _) =>
        RewriteResponse("quView" :: Nil, Map("_source" -> sSource, "_folder" -> sFolder, "_object" -> sObject))
      case RewriteRequest(ParsePath(List("view", sSource, sFolder), _, _, _), _, _) =>
        RewriteResponse("quView" :: Nil, Map("_source" -> sSource, "_folder" -> sFolder))
      case RewriteRequest(ParsePath(List("view", sSource), _, _, _), _, _) =>
        RewriteResponse("quView" :: Nil, Map("_source" -> sSource))
      case RewriteRequest(ParsePath(List("questions", "index"), _, _, _), _, _) =>
        RewriteResponse("question_list" :: Nil)
      case RewriteRequest(ParsePath(List("questions"), _, _, _), _, _) =>
        RewriteResponse("question_list" :: Nil)
    }
  }
}

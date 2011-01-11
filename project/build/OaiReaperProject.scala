import sbt.{ProjectInfo, DefaultProject}

/**
 * Created by IntelliJ IDEA.
 * User: schuller
 * Date: 1/9/11
 * Time: 21:38
 * To change this template use File | Settings | File Templates.
 */

class OaiReaperProject(info: ProjectInfo) extends DefaultProject(info) {
  // val jbossReleases = "JBoss Releases" at "http://repository.jboss.org/maven2/"
  val cmis = "org.smop" %% "cmis" % "0.1-SNAPSHOT" % "compile"
  val dispatch = "net.databinder" %% "dispatch-http" % "0.7.8" % "compile"
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.6" % "test->default"

  override def compileOptions = super.compileOptions ++ Seq(Unchecked)
}

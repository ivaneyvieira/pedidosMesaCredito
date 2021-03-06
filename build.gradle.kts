import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val vaadinonkotlin_version = "1.0.3"
val vaadin10_version = "14.4.3"
val kotlin_version = "1.4.32"
val spring_boot_version = "2.3.1.RELEASE"

plugins {
  id("org.springframework.boot") version "2.3.1.RELEASE"
  id("io.spring.dependency-management") version "1.0.9.RELEASE"
  kotlin("jvm") version "1.4.32"
  id("org.gretty") version "3.0.3"
  war
  id("com.vaadin") version "0.14.3.7"
  kotlin("plugin.spring") version "1.4.32"
}

defaultTasks("clean", "vaadinBuildFrontend", "build")

repositories {
  mavenCentral()
  jcenter() // for Gretty runners
  maven {
    url = uri("https://maven.vaadin.com/vaadin-addons")
  }
}

gretty {
  contextPath = "/"
  servletContainer = "jetty9.4"
}

val staging by configurations.creating

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

group = "pedidosMesaCredito"

dependencies {
  //Spring
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.session:spring-session-core")
  providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
  // Vaadin-on-Kotlin dependency, includes Vaadin
  implementation("com.github.mvysny.karibudsl:karibu-dsl:$vaadinonkotlin_version")
  //implementation("eu.vaadinonkotlin:vok-framework-v10-vokdb:$vaadinonkotlin_version")
  // Vaadin 14
  implementation("com.vaadin:vaadin-core:$vaadin10_version")
  implementation("com.vaadin:vaadin-spring-boot-starter:$vaadin10_version")
  providedCompile("javax.servlet:javax.servlet-api:3.1.0")
  
  implementation("com.zaxxer:HikariCP:3.4.1")
  // logging
  // currently we are logging through the SLF4J API to LogBack. See src/main/resources/logback.xml file for the logger configuration
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("org.slf4j:slf4j-api:1.7.30")
  //implementation("org.slf4j:slf4j-simple:1.7.30")
  implementation("org.sql2o:sql2o:1.6.0")
  implementation("mysql:mysql-connector-java:5.1.49")
  implementation("com.zaxxer:HikariCP:3.4.1")
  implementation("org.imgscalr:imgscalr-lib:4.2")
  implementation("com.jcraft:jsch:0.1.55")
  implementation("org.cups4j:cups4j:0.7.6")
  // https://mvnrepository.com/artifact/com.beust/klaxon
  implementation ("com.beust:klaxon:5.5")
  // https://mvnrepository.com/artifact/com.konghq/unirest-java
  implementation("com.konghq:unirest-java:3.11.11")


  // logging
  // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
  //implementation("com.github.appreciated:app-layout-addon:3.0.0.beta5")
  implementation("org.vaadin.tatu:twincolselect:1.2.0")
  implementation("org.vaadin.gatanaso:multiselect-combo-box-flow:1.1.0")
  implementation("org.vaadin.tabs:paged-tabs:2.0.1")
  implementation("org.claspina:confirm-dialog:2.0.0")
  implementation("org.vaadin.olli:clipboardhelper:1.1.2")
  implementation("com.flowingcode.addons:font-awesome-iron-iconset:2.1.1")
  implementation("com.github.nwillc:poink:0.4.6")
  //implementation("net.sourceforge.dynamicreports:dynamicreports-core:6.11.1")
  implementation("net.sf.jasperreports:jasperreports:6.16.0")
  implementation("com.lowagie:itext:2.1.7")

  // https://mvnrepository.com/artifact/net.sf.jasperreports/jasperreports-fonts
  implementation("net.sf.jasperreports:jasperreports-fonts:6.12.2")
  implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.0"))

  // define any required OkHttp artifacts without version
  implementation("com.squareup.okhttp3:okhttp")
  implementation("com.squareup.okhttp3:logging-interceptor")

  //  compile("org.webjars.bowergithub.vaadin:vaadin-combo-box:4.2.7")
  //compile("com.github.appreciated:app-layout-addon:4.0.0.rc4")
  implementation("org.vaadin.crudui:crudui:4.1.0")
  //compile("com.flowingcode.addons.applayout:app-layout-addon:2.0.2")
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))
  //compile("org.jetbrains.kotlin:kotlin-reflect")
  // test support
  testImplementation("com.github.mvysny.kaributesting:karibu-testing-v10:1.1.16")
  testImplementation("com.github.mvysny.dynatest:dynatest-engine:0.15")
}

vaadin {
  productionMode = false
  pnpmEnable = false
}




plugins {
    id("org.springframework.boot") version("2.7.2")
    id("io.spring.dependency-management") version("1.0.12.RELEASE")
    id("java")
}

group = "com.fisherl"
version = "0.0.1-SNAPSHOT"
//sourceCompatibility = "17"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.webjars:webjars-locator-core")
    implementation("org.webjars:js-cookie:2.1.0")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:2.3.3.RELEASE")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")
    implementation("com.h2database:h2")
//    implementation("org.xerial:sqlite-jdbc:3.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
//    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.hibernate.search:hibernate-search-mapper-orm:6.1.1.Final")
    implementation("org.hibernate.search:hibernate-search-backend-lucene:6.1.1.Final")
}

tasks {

     bootJar {
        archiveFileName.set("SchoolWebsite.jar")
    }
}

//
//tasks.named("test") {
//    useJUnitPlatform()
//}

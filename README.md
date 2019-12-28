# cybersecuritybase-project

[Application repository here](https://github.com/mvarilo/cybersecuritybase-project)

Course project implementing 5 security issues from [OWASP 2017 Top 10](https://www.owasp.org/images/7/72/OWASP_Top_10-2017_%28en%29.pdf.pdf).

## Running the application

How to run from command line:
```
git clone https://github.com/mvarilo/cybersecuritybase-project
cd cybersecuritybase-project
mvn spring-boot:run
```

## Issues

### FLAW 1: A6:2017-Security Misconfiguration

#### Description

Application has a security misconfiguration conserning h2-console which allows you to access the SQL database within the browser. It may be useful during development to have the ability to access the console but in production it's not safe.

#### Reproduction

1. Open localhost:8080 in the browser
2. Log in as 'ted' with password 'ted'.
3. Open 'localhost:8080/h2-console' in the browser.
4. H2-console will appear.

#### Fix

1. Navigate to Java package sec.project.config's class SecurityConfiguration.java
2. Add line '.antMatchers("/h2-console/*").denyAll()' after 'http.authorizeRequests()'

This will prevent anyone from accessing the h2-console.

...

FLAW 2:
<description of flaw 2>
<how to fix it>

...

FLAW 5:
<description of flaw 5>
<how to fix it>
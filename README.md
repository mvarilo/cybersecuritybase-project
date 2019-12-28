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
2. Log in as 'ted' with password '1234'.
3. Open 'localhost:8080/h2-console' in the browser.
4. H2-console will appear.

#### Fix

1. Navigate to Java package sec.project.config's class SecurityConfiguration.java
2. Add line '.antMatchers("/h2-console/*").denyAll()' after 'http.authorizeRequests()'. This will prevent anyone from accessing the h2-console.

### FLAW 2: A3:2017-Sensitive Data Exposure

#### Description

Application is supposed to only show data to authorized users however it does not protect data from being accessed when you are not logged in.

#### Reproduction

1. Open localhost:8080 in the browser
2. Log in as 'ted' with password '1234'.
3. Type in anything you wish to fill the form and click submit.
4. On the '/done' page you should see your submitted signup.
5. Click logout.
6. Go to 'localhost:8080/done'.
7. You should be able to see all the signups even though you are not logged in.

Note: You are also able to delete the data, more on that in FLAW 5.

#### Fix

1. Navigate to Java package sec.project.config's class SecurityConfiguration.java
2. Replace line '.anyRequest().permitAll();' with '.anyRequest().authenticated();'. This should prevent you from accessing signups if you are not logged in.


### FLAW 3: A7:2017-Cross-Site Scripting (XSS)

#### Description

The application stores unsanitized user input that can be viewed at a later time by another user or an administrator. You can execute malicious code by storing it in the address field. Stored XSS is often considered high or critical risk.

#### Reproduction

1. Open localhost:8080 in the browser
2. Log in as 'ted' with password '1234'.
3. In the from address field type '<script>alert("A7:2017");</script>' and click submit.
4. Alert popup that says 'A7:2017' should appear.

#### Fix

1. Navigate to done.html in src/main/resources/templates
2. Change all instances of 'th:utext' to 'th:text'. Unescaped text (utext) displays the original text where as escaped text (text) won't allow scripts to be executed.

### FLAW 4: A2:2017-Broken Authentication

#### Description

Application permits brute force or other automated attacks with weak, default or, well-known passwords. If we know valid usernames for the service or guess that admin is a valid username we can brute force the way in. 

#### Reproduction

1. Launch a program, such as OWASP ZAP that's capable of intercepting, modifying and sending http requests.
2. Utilize a list of [top 10000 most common passwords list found online](https://github.com/danielmiessler/SecLists/tree/master/Passwords).
3. Send POST requests to 'http://localhost:8080/login' where 'username=admin' and 'password=X' where X is from the list of 10000 top most common passwords.
4. After sending the list of POST requests, sort responses by header size. The one with the correct password should be the smallest header size and it's the only one that sends you to 'http://localhost:8080/' instead of 'http://localhost:8080/login?error'.

#### Fix

1. Navigate to Java package sec.project.config's class SecurityConfiguration.java
2. Replace line 'http.csrf().disable();' with 'http.csrf();'. This will require you to provide a CSRF token when you are handling data and as such you're not able to figure out what the password is by fuzzing.

However that's far from a complete solution. Other fixes would be to require accounts to have more complex passwords and recording how many times someone fails to login from the same IP. After a certain amount of attemps IP would be blocked from trying to log in.

### FLAW 5: A5:2017-Broken Access Control

#### Description

Access control enforces policy such that users cannot act outside their intended permissions. Currently it's possible for users to remove signups that they should not be able to remove.

#### Reproduction

1. Open localhost:8080 in the browser
2. Log in as 'ted' with password '1234'.
3. Type in anything you wish to fill the form and click submit.
4. On the '/done' page you should see your submitted signup. Take note of the ID.
5. Click logout.
6. Log in as 'roger' with password 'qwerty'.
7. Type in anything you wish to fill the form and click submit.
8. On the '/done' page you should see your (roger's) submitted signup but not ted's signup.
9. Remembering the ID you can type 'localhost:8080/clear/id' where id is the id you want to delete.
10. Logout. 
11. Log back in as 'ted' and fill the form like before.
11. You will note that the ID that you cleared when you were logged in as 'roger' has been deleted even though you shouldn't have access to it.

Note: In FLAW 2 we were able to access sensitive data even though we were not logged in. If you've not provided the fix detailed in FLAW 2 you're also able to delete data just by going to 'localhost:8080/done' even if you're not logged in.

#### Fix

1. Navigate to Java package sec.project.controller's class SignupController.java
2. Delete line 'signupRepository.delete(id);'
3. Add instead if statement
```
if (signupRepository.findById(id).getName().equals(accountRepository.findByUsername(auth.getName()).getUsername())) {
            signupRepository.delete(id);
        }
```
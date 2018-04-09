## CONGA - Custom Handlebars expressions for AEM

By using CONGA handlebars helper plugins it is possible to extend handlebars by registering custom expressions. Out of the box CONGA AEM plugin ships with a set of built-in custom expressions documented in this chapter.

The basic handlebars expressions are documented in the [Handlebars quickstart][handlebars-quickstart]. CONGA itself also ships with a set of [Custom Handlebars expressions][conga-handlebars-helpers].


### aemCryptoEncrypt

_This works only with (unobfuscated) crypto keys for AEM 6.3 an up._

Encrypts a password or other secret with the AEM crypto AES key.

```
{{aemCryptoEncrypt passwordVariable}}
```

This requires a plugin configuration that defines the path to the AEM crypto key to use (which has to be deployed to the target instances as well):

```xml
<plugin>
  <groupId>io.wcm.devops.conga</groupId>
  <artifactId>conga-maven-plugin</artifactId>
  <extensions>true</extensions>
  <configuration>
    <pluginConfig>
      aem-plugin;cryptoAesKeyUrl=classpath:/crypto/master
    </pluginConfig>
  </configuration>
</plugin>
```

It is recommended to encrypt the key in your SCM using Ansible Vault and reference it like this: `aem-plugin;cryptoAesKeyUrl=ansible-vault:classpath:/crypto/master`. You can store it on other locations as well (filesystem, Maven, HTTP etc.).

If you want to write a generic template that runs with and without having a crypto key available you can add a `ignoreMissingKey` parameter - but in this case the password or secret is inserted unencrypted if the key is missing!

```
{{aemCryptoEncrypt passwordVariable ignoreMissingKey=true}}
```


### oakPasswordHash

Generates a password hash for an Oak JCR user from a plain text password.

```
{{oakPasswordHash passwordVariable}}
```


### oakAuthorizableUuid

Generates a UUID for an authorizable node by deriving it from the authorizable Id.

```
{{oakAuthorizableUuid authorizableId}}
```

### url

Generates or manipulates URLs.

#### Example 1:

```
{{url "localhost" port=8080 scheme="http" path="/custom/path" query="param1=value1&param2=value2" fragment="deeplink"}}
```
**Result:**
```
http://localhost:8080/custom/path?param1=value1&param2=value2#deeplink
```

#### Example 2:

```
{{url "https://localhost/custom/path?param1=value1&param2=value2#deeplink" port=8443 path="" query="" fragment="" }}
```
**Result:**
```
https://localhost:8443
```

### webconsolePasswordHash

Generates a password hash for the  Apache Felix Webconsole (felix.webconsole.password).

```
{{webconsolePasswordHash passwordVariable}}
```


### aemHttpdFilter

Generates HTTPd allow from/required rules for a filter expression. Supports both Apache 2.2 and 2.4. See [CONGA AEM Definitions][aem-definitions] for an usage example.

```
# Location filter
{{#each httpd.accessRestriction.locationFilter~}}
{{aemHttpdFilter this allowFromKey="httpd.accessRestriction.adminAccessFromIp" allowFromHostKey="httpd.accessRestriction.adminAccessFromHost"}}
{{/each~}}
```


### aemDispatcherFilter

Generates AEM dispatcher filter rules for a filter expression. See [CONGA AEM Definitions][aem-definitions] for an usage example.

```
  /filter
    {
{{~#each dispatcher.filter}}
      /{{@index}}
        {{{aemDispatcherFilter this}}}
{{~/each}}
    }
```



[handlebars-quickstart]: ../../handlebars-quickstart.html
[conga-handlebars-helpers]: ../../handlebars-helpers.html
[aem-definitions]: ../../definitions/aem/

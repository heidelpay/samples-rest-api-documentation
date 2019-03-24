# Documenting Apis 

Samples for an article we published in the Javamagazin: https://jaxenter.de/ausgaben/java-magazin-5-19

This project provides samples for building up efficient documentation, covering the following general aspects:

* build documentation with [asciidoctor](https://asciidoctor.org/)
* integrate content from Confluence via the Confluence REST Api. (hardly inspired by inspired by <https://gist.github.com/melix/6020336>)
* integrate diagram based on textural description with [plantUML](http://plantuml.com), 

and, more specific to REST-API'S

* Generate Api-Docs with [Spring Rest Docs](https://spring.io/projects/spring-restdocs) 
* Extended automation based on [Spring Auto Rest Doc](https://github.com/ScaCap/spring-auto-restdocs)
* build OpenApi-Spec from Spring (auto) Rest Docs based on [restdocs-api-spec](https://github.com/ePages-de/restdocs-api-spec)

Furthermore the following options for publishing the docs are covered:

* Html, pdf, etc via asciidoctor
* Api-Doc integrated in the standard asciidoctor output
* Api-Explorer based on [Slate](https://github.com/lord/slate) 
* Api-Explorer based on OpenApi-spec collections with [ReDoc](https://github.com/Rebilly/ReDoc)
* [Swagger-UI](https://swagger.io/tools/swagger-ui/) based on OpenApi-Spec 

## Usage

### Build all
The `docs`-task builds the asciidoctor html-doc and the Markdown-File to be used as input for slate. Since by it uses the importConfluence task it has to be called like this:

`gradlew docs -Dorg.gradle.daemon=false --console=plain`

The build directory will than contain the following:

* docs/html5/index.html: the HTML containinng the Confluence-Content, the diagram, as defined under src/docs/diagrams and the Api-Spec, generated from the executed tests.
* slate/index.html.md: a Markdown containing the Api-Spec to be copied to your slate instance

NOTE: Sometimes you might got a broken build with some weird error message from the diagam plugin: 'asciidoctor-diagram' could not be loaded. Make sure the gem folder gots deleted from your build folder, and try again. 
If you are not interested in integrating the diagram, adjust the dependsOn in the asciidoctor config in build.gradle.


### Configure Confluence Import
The URL to your Confluence Api is read in from the console input, together with the Api-Credentials. The pages to be imported are defined in the importConfluence task itself.
See confluence.gradle for an isolated sample.

NOTE: FOr getting the console-input-reader working, you must, whenever a tasks depeneds on `importConfluence`, call gradle with this args: `-Dorg.gradle.daemon=false --console=plain`

Sample generating the snippets based on confluence:
`gradlew -b confluence.gradle importConfluence -Dorg.gradle.daemon=false --console=plain`


### Building sample-documentation together with diagram

`gradlew -b diagram.gradle asciidoctor`

This will build the asciidoc file containing the diagram. If no other snippets (confluence import oder rest-doc tests) are created before, the generated HTML will contain inclusion-errors.

### building openapi/postman Specs
The openapi/postman configs within the build.gradle file are unfortunately hiding the task-definition comming from the gradle extension itself, having this, the task could not be defined as a dependsOn. So, it has to be executed separately:

`gradlew openapi3`or `gradlew postman`

There is also a isolated version in the openapi.gradle build file.

### Publish to ReDoc / Swagger UI


After running the openapi3 task, simply copy the build/openapi/openapi3.json to src/main/resources/specs/openapi3.json.

Under src/main/resources/static/redoc you will find the index.html ready to use.

For use the swagger-ui, make sure to put the distribution under /src/main/resources/static/swagger-ui, and adjust the the index.html to render the openapi-spec created:


````
<!-- HTML for static distribution bundle build -->
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>Swagger UI</title>
    <link rel="stylesheet" type="text/css" href="./swagger-ui.css" >
    <link rel="icon" type="image/png" href="./favicon-32x32.png" sizes="32x32" />
    <link rel="icon" type="image/png" href="./favicon-16x16.png" sizes="16x16" />
    <style>
      html
      {
        box-sizing: border-box;
        overflow: -moz-scrollbars-vertical;
        overflow-y: scroll;
      }

      *,
      *:before,
      *:after
      {
        box-sizing: inherit;
      }

      body
      {
        margin:0;
        background: #fafafa;
      }
    </style>
  </head>

  <body>
    <div id="swagger-ui"></div>

    <script src="./swagger-ui-bundle.js"> </script>
    <script src="./swagger-ui-standalone-preset.js"> </script>
    <script>
    window.onload = function() {
      // Begin Swagger UI call region
      const ui = SwaggerUIBundle({
        url: "/specs/openapi3.json",
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
          SwaggerUIBundle.presets.apis,
          SwaggerUIStandalonePreset
        ],
        plugins: [
          SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: "StandaloneLayout"
      })
      // End Swagger UI call region

      window.ui = ui
    }
  </script>
  </body>
</html>
````





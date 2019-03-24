// inspired by https://gist.github.com/melix/6020336

/**
@Grapes([@Grab('net.sourceforge.htmlcleaner:htmlcleaner:2.4')])
**/
import org.htmlcleaner.*
import groovyx.net.http.RESTClient

def cleaner = new HtmlCleaner()
def serializer = new SimpleHtmlSerializer(cleaner.properties)

// read confluence credentials from console
def confluenceApi = System.console().readLine('confluence url: ')
def username = System.console().readLine('confluence user: ') 
def password = System.console().readPassword('confluence password: ')

def confluence = new RESTClient( confluenceApi )


def handleEmoticonAsAscii(node, cssClass) {
    node?.name = 'div'
	node.removeAllChildren()
    if(cssClass.contains('emoticon-plus')) {
        node.insertChild(0, new ContentNode('&#10004;'))
    } else if(cssClass.contains('emoticon-minus')) {
       node.insertChild(0, new ContentNode('&#10060;'))   
    } else {
        println "ERROR: UNKNOWN EMOTICON " + cssClass       
    }
}

def replaceAdmonitionNode(node, type) {
	def text = node.children?.find{it -> it.name == 'div'}?.text
	if(text) {
	    node.removeAllChildren()
	    node.insertChild(0, new ContentNode("${type}: ${text}"))
	}
}

def handleAdmonition(node) {
	if('info'.equals(node?.attributes?.get('data-macro-name')) ) {
		replaceAdmonitionNode(node, "NOTE")
	}
    if('tip'.equals(node?.attributes?.get('data-macro-name')) ) {
		replaceAdmonitionNode(node, "TIP")
	}
	 if('warning'.equals(node?.attributes?.get('data-macro-name')) ) {
		replaceAdmonitionNode(node, "IMPORTANT")
	}        
}


def handleNode( node ) {
	if(node instanceof TagNode) {
         if(node?.attributes?.class?.contains( 'emoticon' ) ) {
			handleEmoticonAsAscii(node, node?.attributes?.class)        	
		}
		handleAdmonition(node)                     
	}
}

pages.each {page ->
    def response = confluence.get(path:page.id,  
    		query:['expand': 'body.view'],
    		headers:  [
            'Authorization': "Basic " + ("$username:$password")
            					.bytes.encodeBase64().toString(),
            'Content-Type':'application/json; charset=utf-8'
    ])
 	def tmpHtml = File.createTempFile('clean', 'html')
    def result = cleaner.clean(response.data.body.view.value)
    
	result.traverse({ tagNode, htmlNode ->
		handleNode(htmlNode);		
		handleNode(tagNode);		
		tagNode?.attributes?.remove 'class'
        if ('td' == tagNode?.name || 'th'==tagNode?.name) {
            tagNode.name='td'
            String txt = tagNode.text
            tagNode.removeAllChildren()
            tagNode.insertChild(0, new ContentNode(txt))
        } 
		true} as TagNodeVisitor)
    
    serializer.writeToFile(
		result, tmpHtml.absolutePath, "utf-8"
    )
    def target = outputDir.absolutePath + '/' + page.title
   
   "pandoc -f html -t asciidoc $tmpHtml -o ${target}.adoc".execute().waitFor()
   "pandoc -f html -t gfm $tmpHtml -o ${target}.md".execute().waitFor()
   
   tmpHtml.delete()   
}

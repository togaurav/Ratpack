package org.ratpackframework.app.internal

import org.ratpackframework.templating.TemplateRenderer
import org.ratpackframework.routing.Router
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.handler.ResourceHandler
import org.slf4j.LoggerFactory

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@CompileStatic
class RatpackHandler extends AbstractHandler {

  protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass())

  private final Router router
  private final TemplateRenderer renderer
  private ResourceHandler resourceHandler

  RatpackHandler(Router router, TemplateRenderer renderer, ResourceHandler resourceHandler) {
    this.router = router
    this.renderer = renderer
    this.resourceHandler = resourceHandler
  }

  @Override
  void handle(String target, Request baseRequest, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException, ServletException {
    def responder = router.route(servletRequest)

    def bytes = new ByteArrayOutputStream()
    def status = 200
    def headers = [:]

    Closure<?> handler

    if (responder == null) {
      resourceHandler.handle(target, baseRequest, servletRequest, servletResponse)
      if (baseRequest.isHandled()) {
        return
      } else {
        handler = { status = new OutputStreamWriter(bytes).withWriter { Writer writer -> this.notFound(servletRequest, writer) } }
      }
    } else {
      try {
        def response = responder.respond()
        handler = {
          headers = response.headers
          status = response.status
          bytes << response.bytes
        }
      } catch (Exception e) {
        handler = {
          status = new OutputStreamWriter(bytes).withWriter { Writer writer -> this.error(servletRequest, e, writer) }
        }
      }
    }

    handler()

    servletResponse.status = status
    headers.each { k, v ->
      (v instanceof Iterable ? v.toList() : [v]).each {
        servletResponse.addHeader(k.toString(), it.toString())
      }
    }
    servletResponse.setContentLength(bytes.size())
    servletResponse.outputStream << bytes
    baseRequest.handled = true
    logger.info("[   ${status}] ${servletRequest.method} ${servletRequest.pathInfo}")
  }

  private int error(HttpServletRequest servletRequest, Exception error, Writer writer) {
    StackTraceUtils.deepSanitize(error)
    error.printStackTrace()
    writer << renderer.renderException(error, servletRequest)
    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
  }

  private int notFound(HttpServletRequest servletRequest, Writer writer) {
    writer << renderer.renderError(
        title: 'Page Not Found',
        message: 'Page Not Found',
        metadata: [
            'Request Method': servletRequest.method.toUpperCase(),
            'Request URL': servletRequest.requestURL,
        ]
    )
    HttpServletResponse.SC_NOT_FOUND
  }

}

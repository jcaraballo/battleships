package org.casa.battleships.frontend

import dispatch._
import com.ning.http.client.Response


class Transport(val baseUrl: String) {
  def getWithStatusCode(s: String): (Int, String) = {
    val response: Response = Http(url(baseUrl + s))()
    (response.getStatusCode, response.getResponseBody)
  }

  def post(path: String, requestBody: String): String = {
    Http(url(baseUrl + path).POST << requestBody OK as.String)()
  }

  def get(path: String): String = {
    Http(url(baseUrl + path) OK as.String)()
  }

  def sub(path: String): Transport = {
    new Transport(baseUrl + path)
  }
}
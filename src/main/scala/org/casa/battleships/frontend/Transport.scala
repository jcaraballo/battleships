package org.casa.battleships.frontend

import dispatch._


class Transport(val baseUrl: String) {
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
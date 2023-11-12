package com.ulbra.metroplanpdfautomation.controllers;

public enum ServletHeaders {
  CONTENT_DISPOSITION_HEADER("Content-Disposition"),
  FILE_ATTACHMENT_HEADER("attachment; filename=declaração_de_presencialidade_");

  public final String value;

  ServletHeaders(String value) {
    this.value = value;
  }
}

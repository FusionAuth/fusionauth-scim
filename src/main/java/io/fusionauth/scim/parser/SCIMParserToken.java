package io.fusionauth.scim.parser;

public class SCIMParserToken {
  public SCIMParserState state;
  public String remaining;
  public String value;

  public SCIMParserToken(SCIMParserState state, String remaining, String value) {
    this.state = state;
    this.remaining = remaining.trim();
    this.value = value;
  }
}

package io.fusionauth.scim.parser;

public enum Op {
  /**
   * Equal. Operator value and attribute value must match exactly
   */
  eq,
  /**
   * Not equal. Operator value and attribute value are not identical
   */
  ne,
  /**
   * Contains. Operator value must be a substring of the attribute value
   */
  co,
  /**
   * Starts with. Operator value must be a substring of the attribute value starting from the beginning
   */
  sw,
  /**
   * Ends with. Operator value must be a substring of the attribute value matching at the end
   */
  ew,
  /**
   * Present. The attribute value is not null or empty. Unary operator
   */
  pr,
  /**
   * Greater than. The attribute value is greater than the operator value
   */
  gt,
  /**
   * Greater than or equal. The attribute value is greater than or equal to the operator value
   */
  ge,
  /**
   * Less than. The attribute value is less than the operator value
   */
  lt,
  /**
   * Less than or equal. The attribute value is less than or equal to the operator value
   */
  le
}

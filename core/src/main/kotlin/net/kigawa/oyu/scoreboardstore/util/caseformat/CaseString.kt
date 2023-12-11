package net.kigawa.oyu.scoreboardstore.util.caseformat

@Suppress("unused")
class CaseString(
  val values: List<String>,
  private val caseFormat: CaseFormat,
) {
  
  fun toFormatStr(caseFormat: CaseFormat): String {
    return caseFormat.marge(values)
  }
  
  fun toFormat(caseFormat: CaseFormat): CaseString {
    return caseFormat.caseString(values)
  }
  
  override fun toString(): String {
    return caseFormat.marge(values)
  }
}
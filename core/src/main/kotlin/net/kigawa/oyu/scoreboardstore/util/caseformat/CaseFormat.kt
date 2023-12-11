package net.kigawa.oyu.scoreboardstore.util.caseformat

@Suppress("unused")
enum class CaseFormat(
  private val marge: (List<String>)->String,
  private val split: (String)->List<String>,
) {
  
  KEBAB_CASE({
    it.joinToString("-")
  }, {
    it.split("-")
  }),
  HIGHER_CAMEL_CASE({values->
    val sb = StringBuilder()
    values.forEach {
      if (it == "") return@forEach
      
      sb.append(it[0].uppercaseChar()).append(it.drop(1))
    }
    sb.toString()
  }, {string->
    val list = mutableListOf<String>()
    val sb = StringBuilder()
    
    string.forEach {
      if (it.isLowerCase()) {
        sb.append(it)
        return@forEach
      }
      if (sb.isNotEmpty()) {
        list.add(sb.toString())
        sb.clear()
      }
      sb.append(it.lowercaseChar())
    }
    
    list
  });
  
  fun caseString(string: String): CaseString {
    return caseString(split(string))
  }
  
  fun caseString(values: List<String>): CaseString {
    return CaseString(values, this)
  }
  
  fun marge(values: List<String>): String {
    return marge.invoke(values)
  }
  
  fun split(string: String): List<String> {
    return split.invoke(string)
  }
}
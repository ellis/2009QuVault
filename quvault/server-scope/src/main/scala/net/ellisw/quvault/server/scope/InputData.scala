// REFACTOR: Rename file to QuFieldData.scala
package net.ellisw.quvault.server.scope

// REFACTOR: Rename to FieldKind
object QuFieldKind extends Enumeration {
	val SingleLine, MultiLine = Value
}

// REFACTOR: Rename to FieldData
sealed class QuFieldData(
	val id: String,
	val kind: QuFieldKind.Value,
	val attrs: Map[String, String]
)

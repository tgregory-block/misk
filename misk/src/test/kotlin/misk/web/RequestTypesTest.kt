package misk.web

import com.squareup.protos.test.parsing.Shipment
import com.squareup.protos.test.parsing.Warehouse
import misk.web.actions.Field
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.full.createType

internal class RequestTypesTest {

  val requestTypes = RequestTypes()

  @Test fun handlesNull() {
    assertThat(requestTypes.calculateTypes(null)).isEmpty()
  }

  @Test fun handlesNonWireMessages() {
    assertThat(requestTypes.calculateTypes(String::class.createType())).isEmpty()
  }

  @Test fun handlesWireMessages() {
    val types = requestTypes.calculateTypes(Shipment::class.createType())

    // Check Message Types
    assertThat(types).hasSize(2)
    assertThat(types).containsKey(Shipment::class.qualifiedName)
    assertThat(types).containsKey(Warehouse::class.qualifiedName)

    val shipmentType = types.get(Shipment::class.qualifiedName)!!
    val warehouseType = types.get(Warehouse::class.qualifiedName)!!

    // Check primitive types
    assertThat(shipmentType.fields).contains(Field("shipment_id", "Long", false))
    assertThat(warehouseType.fields).contains(Field("warehouse_token", "String", false))

    // Check repeated types
    assertThat(shipmentType.fields).contains(Field("notes", "String", true))
    assertThat(warehouseType.fields).contains(Field("alternates", Warehouse::class.qualifiedName!!, true))
  }
}
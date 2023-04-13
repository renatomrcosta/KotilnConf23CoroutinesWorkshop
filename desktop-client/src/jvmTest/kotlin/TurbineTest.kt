import app.cash.turbine.test
import com.kotlinconf.workshop.kettle.CelsiusTemperature
import com.kotlinconf.workshop.kettle.FahrenheitTemperature
import com.kotlinconf.workshop.kettle.KettlePowerState
import com.kotlinconf.workshop.kettle.network.KettleService
import com.kotlinconf.workshop.kettle.toFahrenheit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class TurbineTest {

    class TestKettleService : KettleService {
        override suspend fun switchOn() {}

        override suspend fun switchOff() {}

        override fun observeTemperature(): Flow<CelsiusTemperature> = flowOf(
            CelsiusTemperature(23.0),
            CelsiusTemperature(123.123),
            CelsiusTemperature(233.0),
        )

        override fun observeKettlePowerState(): Flow<KettlePowerState> = flowOf()
    }

    @Test
    fun `viewmodel should properly convert celsius to fahrenheit`() = runBlocking {
        val testKettleService = TestKettleService()
        testKettleService.observeTemperature().map { it.toFahrenheit() }
            .test {
                assertEquals(FahrenheitTemperature(73.4).value, awaitItem().value, 0.02)
                assertEquals(FahrenheitTemperature(253.62).value, awaitItem().value, 0.02)
                assertEquals(FahrenheitTemperature(451.4).value, awaitItem().value, 0.02)
                awaitComplete()
            }
    }
}

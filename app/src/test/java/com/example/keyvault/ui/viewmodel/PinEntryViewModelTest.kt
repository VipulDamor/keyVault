import com.example.keyvault.ui.component.StepMode
import com.example.keyvault.ui.viewmodel.PinEntryViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PinEntryViewModelTest {

    private lateinit var viewModel: PinEntryViewModel

    @Before
    fun setUp() {
        viewModel = PinEntryViewModel()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun test_number_input_appends_when_pin_length_less_than_six() {
        viewModel.onNumberInput("1")
        viewModel.onNumberInput("2")
        viewModel.onNumberInput("3")
        assertEquals("123", viewModel.currentPin.value)
    }

    @Test
    fun test_number_input_ignored_when_pin_length_is_six() {
        addSixInput()
        viewModel.onNumberInput("7")
        assertEquals("123456", viewModel.currentPin.value)
    }


    @Test
    fun `test onNextClicked Create Step ConfirmPin success`() {

        viewModel.updateFirstPin("123456")

        viewModel.onNextClicked(StepMode.Create, null) {
            val capturedValue = it.first
            assertEquals("123456", capturedValue)
            assertTrue(it.second)
        }

    }

    @Test
    fun `test onNextClicked Create Step Verify success`() {
        addSixInput()

        viewModel.onNextClicked(StepMode.Verify, "123456") {
            val capturedValue = it.first
            assertEquals(null, capturedValue)
            assertTrue(it.second)
        }

    }

    @Test
    fun `test onNextClicked Create Step Verify fail`() {
        addSixInput()
        viewModel.onNextClicked(StepMode.Verify, "123457") {
            val capturedValue = it.first
            assertEquals(null, capturedValue)
            assertTrue(!it.second)
        }

    }

    private fun addSixInput(){
        viewModel.onNumberInput("1")
        viewModel.onNumberInput("2")
        viewModel.onNumberInput("3")
        viewModel.onNumberInput("4")
        viewModel.onNumberInput("5")
        viewModel.onNumberInput("6")

    }
}
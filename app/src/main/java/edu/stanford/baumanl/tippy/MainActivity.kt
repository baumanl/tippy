package edu.stanford.baumanl.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.w3c.dom.Text

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentageLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var sSplit: Switch
    private lateinit var etPartySize: EditText
    private lateinit var tvPartySize: TextView
    private lateinit var tvPerPersonTotal: TextView
    private lateinit var tvPerPerson: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sSplit = findViewById(R.id.sSplit)
        etPartySize = findViewById(R.id.etPartySize)
        tvPartySize = findViewById(R.id.tvPartySize)
        tvPerPerson = findViewById(R.id.tvPerPerson)
        tvPerPersonTotal = findViewById(R.id.tvPerPersonTotal)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentageLabel = findViewById(R.id.tvTipPercentageLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentageLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentageLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }

        })

        sSplit.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                //checked
                Log.i(TAG, "checked")
                etPartySize.visibility = View.VISIBLE
                tvPartySize.visibility = View.VISIBLE
                tvPerPersonTotal.visibility = View.VISIBLE
                tvPerPerson.visibility = View.VISIBLE
                etPartySize.setText("1")
                tvPerPersonTotal.text = tvTotalAmount.text
            } else {
                //not checked
                Log.i(TAG, "not checked")
                etPartySize.visibility = View.INVISIBLE
                tvPartySize.visibility = View.INVISIBLE
                tvPerPersonTotal.visibility = View.INVISIBLE
                tvPerPerson.visibility = View.INVISIBLE

            }
        }

        etPartySize.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (tvTotalAmount.text.isEmpty() || etPartySize.text.isEmpty()) {
                    tvPerPersonTotal.text = ""
                    return
                }
                var total = tvTotalAmount.text.toString().toDouble()
                var partySize = etPartySize.text.toString().toInt()
                var res = total / partySize
                tvPerPersonTotal.text = "%.2f".format(res)
            }

        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        //Update the color based on the tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.worst),
            ContextCompat.getColor(this, R.color.best)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}
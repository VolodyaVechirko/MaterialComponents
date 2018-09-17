package com.vvechirko.testapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.activity_constraint_set.*

class ConstraintSetActivity : AppCompatActivity() {

    var expanded: Boolean = false
    lateinit var state1: ConstraintSet
    lateinit var state2: ConstraintSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constraint_set)

        initStates()
        findViewById<View>(R.id.cardView).setOnClickListener {
            toggle()
        }
    }

    private fun initStates() {
        state1 = ConstraintSet().apply {
            clone(rootContainer)
        }

        state2 = ConstraintSet().apply {
            clone(rootContainer)
            constrainHeight(R.id.cardView, ConstraintSet.MATCH_CONSTRAINT)
            setMargin(R.id.cardView, ConstraintSet.TOP, 0)
            setMargin(R.id.cardView, ConstraintSet.START, 0)
            setMargin(R.id.cardView, ConstraintSet.END, 0)
            setMargin(R.id.cardView, ConstraintSet.BOTTOM, 0)
        }
    }

    override fun onBackPressed() {
        if (expanded) toggle() else super.onBackPressed()
    }

    fun toggle() {
        TransitionManager.beginDelayedTransition(rootContainer)
        val constraint = if (expanded) state1 else state2
        constraint.applyTo(rootContainer)

//        if (expanded) {
//            editName.isEnabled = true
//            editPrice.isEnabled = true
//            editBody.isEnabled = true
//            editBody.setLines(Int.MAX_VALUE)
//        } else {
//            editName.isEnabled = false
//            editPrice.isEnabled = false
//            editBody.isEnabled = false
//            editBody.setLines(4)
//        }
        expanded = !expanded
    }
}
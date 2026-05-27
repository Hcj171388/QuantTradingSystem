package com.quanttrading.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.quanttrading.R

class AnalysisFragment : Fragment() {

    private lateinit var signalCard: MaterialCardView
    private lateinit var factorsCard: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        signalCard = view.findViewById(R.id.signalCard)
        factorsCard = view.findViewById(R.id.factorsCard)
        
        signalCard.setOnClickListener {
            Toast.makeText(requireContext(), "功能开发中", Toast.LENGTH_SHORT).show()
        }
    }
}

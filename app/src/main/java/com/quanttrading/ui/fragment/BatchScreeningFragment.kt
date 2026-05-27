package com.quanttrading.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.quanttrading.R

class BatchScreeningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<View>(R.id.stockCodeInput)?.visibility = View.GONE
        view.findViewById<View>(R.id.searchButton)?.visibility = View.GONE
        view.findViewById<View>(R.id.stockInfoCard)?.visibility = View.GONE
        view.findViewById<View>(R.id.progressBar)?.visibility = View.GONE
        view.findViewById<View>(R.id.errorText)?.visibility = View.GONE
    }
}

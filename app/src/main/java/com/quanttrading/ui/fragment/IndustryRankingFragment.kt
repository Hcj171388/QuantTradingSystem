package com.quanttrading.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.quanttrading.R
import com.quanttrading.data.model.IndustryRank
import com.quanttrading.data.model.IndustryStock
import com.quanttrading.data.repository.StockRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class IndustryRankingFragment : Fragment() {

    private lateinit var queryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var progressLinear: SeekBar
    private lateinit var resultContainer: LinearLayout

    private val repository = StockRepository()
    private val ratioFormat = DecimalFormat("0.0000")
    private val percentFormat = DecimalFormat("0.00")
    private val priceFormat = DecimalFormat("0.00")
    private var isQuerying = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_industry_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        queryButton = view.findViewById(R.id.queryButton)
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)
        progressLinear = view.findViewById(R.id.progressLinear)
        resultContainer = view.findViewById(R.id.resultContainer)

        resultContainer.visibility = View.GONE

        queryButton.setOnClickListener {
            if (!isQuerying) {
                startQuery()
            } else {
                Toast.makeText(requireContext(), "正在查询中...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startQuery() {
        isQuerying = true
        queryButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        progressText.visibility = View.VISIBLE
        progressLinear.visibility = View.VISIBLE
        progressLinear.progress = 0
        resultContainer.visibility = View.GONE
        resultContainer.removeAllViews()

        lifecycleScope.launch {
            try {
                progressText.text = "正在获取行业板块列表..."
                progressLinear.progress = 10

                val ranks = repository.getIndustryRankings(topN = 3).getOrElse { e ->
                    throw e
                }

                if (ranks.isEmpty()) {
                    Toast.makeText(requireContext(), "未获取到行业数据", Toast.LENGTH_LONG).show()
                    return@launch
                }

                progressText.text = "已获取行业排名 Top ${ranks.size}，开始拉取成份股..."
                progressLinear.progress = 30

                // 为每个 Top3 行业拉取成份股
                ranks.forEachIndexed { index, rank ->
                    progressText.text = "正在拉取第 ${index + 1} 个行业（${rank.name}）的成份股..."
                    val progress = 30 + (index + 1) * 70 / ranks.size
                    progressLinear.progress = progress

                    val stocks = repository.getTopStocksForIndustry(rank.code, topN = 10).getOrElse { emptyList() }
                    addIndustrySection(index + 1, rank, stocks)
                }

                progressLinear.progress = 100
                progressText.text = "查询完成"
                resultContainer.visibility = View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "查询失败：${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isQuerying = false
                queryButton.isEnabled = true
                progressBar.visibility = View.GONE
                progressText.visibility = View.GONE
                progressLinear.visibility = View.GONE
            }
        }
    }

    /**
     * 渲染一个行业排名区块：行业头部信息 + 成份股列表
     */
    private fun addIndustrySection(rankIndex: Int, rank: IndustryRank, stocks: List<IndustryStock>) {
        val context = requireContext()

        val card = MaterialCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 12
                bottomMargin = 12
            }
            radius = 12f
            cardElevation = 6f
            useCompatPadding = true
        }

        val innerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 36, 36, 36) // ~12dp
        }

        // 行业头部 - 第 1 行：排名 + 行业名称
        val headerLine1 = TextView(context).apply {
            text = "No.$rankIndex  ${rank.name}"
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 17f)
            setTextColor(Color.parseColor("#212121"))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        innerLayout.addView(headerLine1)

        // 行业头部 - 第 2 行：板块代码 + 成份股数量 + 上涨家数 + 上涨比例
        val headerLine2 = TextView(context).apply {
            text = "代码 ${rank.code}    " +
                    "成份股 ${rank.stockCount}    " +
                    "上涨 ${rank.upCount}    " +
                    "上涨比例 ${ratioFormat.format(rank.ratio)}%"
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(Color.parseColor("#757575"))
        }.also {
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = 6
            innerLayout.addView(it, lp)
        }

        // 行业头部 - 第 3 行：A股总数量
        val headerLine3 = TextView(context).apply {
            text = "A股总数 ${rank.totalAShareCount}    " +
                    "公式：${rank.upCount}/${rank.stockCount}/${rank.totalAShareCount}×10000 = ${ratioFormat.format(rank.ratio)}%"
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#9E9E9E"))
        }.also {
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = 4
            lp.bottomMargin = 12
            innerLayout.addView(it, lp)
        }

        // 分隔线
        val divider = View(context).apply {
            setBackgroundColor(Color.parseColor("#E0E0E0"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
            )
        }
        innerLayout.addView(divider)

        // 表头
        val tableHeader = TextView(context).apply {
            text = "名称        代码         股价       同比营收    同比净利    持仓占比"
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 10f)
            setTextColor(Color.parseColor("#9E9E9E"))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }.also {
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = 12
            lp.bottomMargin = 6
            innerLayout.addView(it, lp)
        }

        // 成份股列表
        val recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = StockAdapter(stocks)
        }
        innerLayout.addView(recyclerView)

        card.addView(innerLayout)
        resultContainer.addView(card)
    }

    /**
     * 成份股 Adapter
     */
    inner class StockAdapter(
        private val stocks: List<IndustryStock>
    ) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.stockName)
            val code: TextView = itemView.findViewById(R.id.stockCode)
            val price: TextView = itemView.findViewById(R.id.stockPrice)
            val revenueYoy: TextView = itemView.findViewById(R.id.revenueYoy)
            val profitYoy: TextView = itemView.findViewById(R.id.profitYoy)
            val holdingRatio: TextView = itemView.findViewById(R.id.holdingRatio)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_industry_stock, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val s = stocks[position]
            holder.name.text = s.name
            holder.code.text = s.code
            holder.price.text = "¥${priceFormat.format(s.price)}"

            holder.revenueYoy.text = formatYoy(s.revenueYoy)
            holder.revenueYoy.setTextColor(yoyColor(s.revenueYoy))

            holder.profitYoy.text = formatYoy(s.profitYoy)
            holder.profitYoy.setTextColor(yoyColor(s.profitYoy))

            holder.holdingRatio.text = "${percentFormat.format(s.holdingRatio)}%"
        }

        override fun getItemCount(): Int = stocks.size

        private fun formatYoy(value: Double): String {
            return if (value == 0.0) "N/A" else "${percentFormat.format(value)}%"
        }

        private fun yoyColor(value: Double): Int {
            return when {
                value > 0 -> Color.parseColor("#D32F2F") // 红色（增长）
                value < 0 -> Color.parseColor("#388E3C") // 绿色（下降）
                else -> Color.parseColor("#9E9E9E")
            }
        }
    }
}

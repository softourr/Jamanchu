package com.autoever.jamanchu.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autoever.jamanchu.R
import com.autoever.jamanchu.activities.LineActivity
import com.autoever.jamanchu.api.RetrofitInstance
import com.autoever.jamanchu.models.Line
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LineFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LineAdapter
    private val lines = mutableListOf<Line>()
    private lateinit var floatingActionButton: FloatingActionButton
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_line, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = LineAdapter(lines, this::onEditClicked, this::onDeleteClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        floatingActionButton = view.findViewById(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), LineActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_LINE) // 새 라인 추가
        }

        fetchLines()
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && (requestCode == REQUEST_CODE_ADD_LINE || requestCode == REQUEST_CODE_EDIT_LINE)) {
            fetchLines() // 리스트 갱신
        }
    }

    private fun fetchLines() {
        // 통신
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getLines()
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        lines.clear()
                        lines.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun onEditClicked(line: Line) {
        if (line.user == firebaseAuth.currentUser?.uid) {
            val intent = Intent(requireContext(), LineActivity::class.java).apply {
                putExtra("lineId", line.id)
                putExtra("lineContent", line.line)
            }
            startActivityForResult(intent, REQUEST_CODE_EDIT_LINE)
        }
    }

    private fun onDeleteClicked(line: Line) {
        if (line.user == firebaseAuth.currentUser?.uid) {
            deleteLine(line.id)
        }
    }

    private fun deleteLine(id: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.deleteLine(id)
                if (response.isSuccessful) {
                    fetchLines() // 리스트 갱신
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD_LINE = 100
        private const val REQUEST_CODE_EDIT_LINE = 101
    }
}

class LineAdapter(
    private val lines: List<Line>,
    private val onEditClicked: (Line) -> Unit,
    private val onDeleteClicked: (Line) -> Unit
) : RecyclerView.Adapter<LineAdapter.LineViewHolder>() {

    class LineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val buttonEdit: View = view.findViewById(R.id.buttonEdit)
        val buttonDelete: View = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_line, parent, false)
        return LineViewHolder(view)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val line = lines[position]
        holder.textView.text = line.line

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        holder.buttonEdit.visibility = if (line.user == currentUserId) View.VISIBLE else View.INVISIBLE
        holder.buttonEdit.setOnClickListener {
            onEditClicked(line)
        }

        holder.buttonDelete.visibility = if (line.user == currentUserId) View.VISIBLE else View.INVISIBLE
        holder.buttonDelete.setOnClickListener {
            onDeleteClicked(line)
        }
    }

    override fun getItemCount(): Int = lines.size
}
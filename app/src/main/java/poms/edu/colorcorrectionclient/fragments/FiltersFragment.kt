package poms.edu.colorcorrectionclient.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.jetbrains.anko.toast
import poms.edu.colorcorrectionclient.adapters.FilterItem
import poms.edu.colorcorrectionclient.adapters.FiltersAdapter
import poms.edu.colorcorrectionclient.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FiltersFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FiltersFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FiltersFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var items: List<FilterItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_filters, container, false)
        val recyclerView: RecyclerView = layout.findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayout.HORIZONTAL,
                false
            )
            adapter = FiltersAdapter(items) { filterItem, position ->
                toast("$position")
                val url = "http://10.0.2.2:5000/get_grid_by_name?name=a"
                val que = Volley.newRequestQueue(activity)
                val req = StringRequest(
                    Request.Method.GET,
                    url,
                    Response.Listener {
                        toast(it.toString())
                    },
                    Response.ErrorListener {

                    })
                que.add(req)
            }
        }
        return layout
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(items: List<FilterItem>): FiltersFragment =
                FiltersFragment().apply {
                    this.items = items
                }
    }
}

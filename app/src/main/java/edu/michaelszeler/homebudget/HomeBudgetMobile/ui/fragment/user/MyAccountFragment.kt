package edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.user

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.session.SessionManager
import edu.michaelszeler.homebudget.HomeBudgetMobile.ui.fragment.MainMenuFragment
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.FragmentNavigationUtility
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationHost
import edu.michaelszeler.homebudget.HomeBudgetMobile.utils.navigation.NavigationIconClickListener
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import org.json.JSONObject

class MyAccountFragment : Fragment() {

    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sessionManager = SessionManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_my_account, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar_my_account)

        view.toolbar_my_account.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!,
                view.nested_scroll_view_my_account,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon),
                ContextCompat.getDrawable(context!!, R.drawable.menu_icon))
        )

        FragmentNavigationUtility.setUpMenuButtons((activity as NavigationHost), view)

        val login = sessionManager.getUserDetails()?.get("login")
        val password = sessionManager.getUserDetails()?.get("password")
        val requestQueue : RequestQueue = Volley.newRequestQueue(activity)

        val jsonObjectRequest = object: JsonObjectRequest(
                Method.GET,
                "http://10.0.2.2:8080/user",
                null,
                { response: JSONObject? ->
                    run {
                        Log.e("Rest Response", response.toString())
                        view.text_view_my_account_username.text = response?.getString("login")
                        view.text_view_my_account_first_name.text = response?.getString("firstName")
                        view.text_view_my_account_last_name.text = response?.getString("lastName")
                        relative_layout_my_account.visibility = View.VISIBLE
                        loading_panel_my_account.visibility = View.GONE
                    }
                },
                { error: VolleyError? ->
                    run {
                        Log.e("Error rest response", error.toString())
                        val networkResponse: NetworkResponse? = error?.networkResponse
                        val jsonError : String? = String(networkResponse?.data!!)
                        val answer = JSONObject(jsonError ?: "{}")
                        if (answer.has("message")) {
                            Log.e("Error rest data", answer.getString("message") ?: "empty")
                            when (answer.getString("message")) {
                                "incorrect username" -> {
                                    view.text_input_layout_login_login.error =
                                            "Incorrect login"
                                }
                                "Bad credentials" -> {
                                    Toast.makeText(activity, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(activity, "Server error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(activity, "Unknown server error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = String.format("Basic %s", Base64.encodeToString(String.format("%s:%s", login, password).toByteArray(), Base64.NO_WRAP))
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_back, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_go_back -> {
                (activity as NavigationHost).navigateTo(MainMenuFragment(), false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
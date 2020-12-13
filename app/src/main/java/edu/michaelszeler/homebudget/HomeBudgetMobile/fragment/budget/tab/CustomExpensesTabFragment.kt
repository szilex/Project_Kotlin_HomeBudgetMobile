package edu.michaelszeler.homebudget.HomeBudgetMobile.fragment.budget.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.michaelszeler.homebudget.HomeBudgetMobile.R
import edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.expense.CustomExpenseCardRecyclerViewAdapter
import edu.michaelszeler.homebudget.HomeBudgetMobile.adapter.strategy.StrategyGridItemDecoration
import edu.michaelszeler.homebudget.HomeBudgetMobile.model.expense.CustomExpenseEntry
import kotlinx.android.synthetic.main.fragment_current_budget_custom_expenses_tab.view.*

class CustomExpensesTabFragment(private val expenses : List<CustomExpenseEntry>) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view : View = inflater.inflate(R.layout.fragment_current_budget_custom_expenses_tab, container, false)

        view.recycler_view_custom_expenses_tab.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)

        view.recycler_view_custom_expenses_tab.layoutManager = gridLayoutManager
        val adapter = CustomExpenseCardRecyclerViewAdapter(expenses, fragmentManager)
        view.recycler_view_custom_expenses_tab.adapter = adapter

        val largePadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing)
        val smallPadding = resources.getDimensionPixelSize(R.dimen.strategy_grid_spacing_small)
        view.recycler_view_custom_expenses_tab.addItemDecoration(StrategyGridItemDecoration(largePadding, smallPadding))

        return view
    }
}
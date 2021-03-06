package com.andres.myrecipes.ui.categories

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.andres.myrecipes.*
import com.andres.myrecipes.adapters.MealsCategoryAdapter
import com.andres.myrecipes.databinding.FragmentCategoryBinding
import com.andres.myrecipes.models.*
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFragment : Fragment(R.layout.fragment_category) {
    lateinit var binding: FragmentCategoryBinding
    var listCategory = arrayListOf<CategoryChip>()
        var category = "Beef"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCategoryBinding.bind(view)
        getCategories(view)
        getMealsCategory()
        if (savedInstanceState !== null){
            category = savedInstanceState.getString("category").toString()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("category", category)
    }

    private fun getCategories(view: View) {
        val service: ApiService = RetrofitEngine.retrofit()
            .create(ApiService::class.java)
        val result: Call<Category> = service.getCategories()
        result.enqueue(object : Callback<Category> {
            override fun onFailure(call: Call<Category>, t: Throwable) {
                Toast.makeText(requireContext(), "error al carga datos ${t}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                listCategory = response.body()!!.meals
                Log.d("lista",listCategory.toString())
                renderChip(view)
            }


        })


    }


    private fun getMealsCategory() {

        val service: ApiService = RetrofitEngine.retrofit()
            .create(ApiService::class.java)
        val result: Call<CategoryList> = service.getMealsByCategory(category)
        result.enqueue(object : Callback<CategoryList> {
            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Toast.makeText(requireContext(), "error al carga datos", Toast.LENGTH_LONG).show()
            }

            override fun onResponse( call: Call<CategoryList>,response: Response<CategoryList>) {
                val list = response.body()!!.meals

                binding.progressCategory.visibility = View.GONE
                binding.recycleViewResult.visibility = View.VISIBLE
                binding.chipScroll.visibility = View.VISIBLE
                initRecycler(list,category)


            }


        })


    }

    fun addCategoryNameTolist(list: ArrayList<CategoryResponse>,name:String): ArrayList<CategoryItem> {
        Log.d("namelist", list.toString())
        val listmeals = arrayListOf<CategoryItem>()
        list.map { item ->

            listmeals.add(CategoryItem(item.strMeal,item.strMealThumb,name, item.idMeal))

        }
        return listmeals

    }
    private fun renderChip(view: View) {

        for (value in listCategory) {
            Log.d("nombre", value.strCategory)
            val chip = Chip(view.context)

            chip.setOnClickListener {
                category = value.strCategory
             getMealsCategory()
            }

            chip.text = value.strCategory

            chip.height = 60
            chip.chipStartPadding = 8f
            chip.chipEndPadding = 8f
            chip.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(view.context, R.color.primary))
            chip.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        view.context,
                        R.color.icons
                    )
                )
            )

            binding.ChGroup.addView(chip)
        }
    }


    fun initRecycler(list: ArrayList<CategoryResponse>,name:String) {
        Log.d("Init", list.toString())
        val list=  addCategoryNameTolist(list,name)
        binding.recycleViewResult.adapter = MealsCategoryAdapter(list)
        binding.recycleViewResult.layoutManager = GridLayoutManager(requireContext(), 2)


    }

}
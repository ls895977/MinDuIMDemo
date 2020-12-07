package com.example.minduimdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.css.im_kit.db.AppDatabase
import com.css.im_kit.db.imDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FirstFragment : Fragment(){
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val uiScope = CoroutineScope(Dispatchers.Main)
//        val userInfoDao =  requireContext().imDb().userDao
//        uiScope.launch {
//
//            userInfoDao.getAll().let {
//
//            }
//        }
        view.findViewById<View>(R.id.button_first).setOnClickListener {
            NavHostFragment.findNavController(this@FirstFragment)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

}
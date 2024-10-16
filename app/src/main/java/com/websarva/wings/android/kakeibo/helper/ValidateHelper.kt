package com.websarva.wings.android.kakeibo.helper

import android.content.Context
import android.os.Build
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import com.websarva.wings.android.kakeibo.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 入力フォームのバリデーションチェック
 */
class ValidateHelper(private val context: Context) {
    //未入力チェック(空白の時false)
    private fun emptyCheck(text:String):Boolean{
        return text != ""
    }

    //電子メールの形式チェック(@が含まれていればtrue)
    private fun emailFormatCheck(email:String):Boolean{
        return email.contains("@")
    }

    /**
     * 桁数チェック
     * @param[text] 桁数チェックをしたい文字列
     * @param[digit] 対象文字列の文字数の下限値
     */
    private fun lengthCheck(text:String,digit:Int):Boolean{
        return text.length >= digit
    }

    //半角数字チェック
    private fun numberFormatCheck(text:String):Boolean{
        val regex = Regex("^[0-9]+$")
        return regex.matches(text)
    }

    /**
     * 電子メールのバリデーションチェック
     */
    fun emailCheck(editTextEmail:EditText):Pair<Boolean,String>{
        val email = editTextEmail.text.toString()
        if(!emptyCheck(email)){
            return Pair(false,context.getString(R.string.error_empty))
        }
        if(!emailFormatCheck(email)){
            return Pair(false,context.getString(R.string.error_email))
        }
        return Pair(true,"")
    }

    /**
     * パスワードのバリデーションチェック
     */
    fun passwordCheck(editTextPassword:EditText):Pair<Boolean,String>{
        val password = editTextPassword.text.toString()
        if(!emptyCheck(password)){
            return Pair(false,context.getString(R.string.error_empty))
        }
        if(!lengthCheck(password,6)){
            return Pair(false,context.getString(R.string.error_digit_6))
        }
        return Pair(true,"")
    }

    /**
     * ユーザー名のバリデーションチェック
     */
    fun usernameCheck(editTextUsername:EditText):Pair<Boolean,String>{
        val username = editTextUsername.text.toString()
        if(!emptyCheck(username)){
            return Pair(false,context.getString(R.string.error_empty))
        }
        return Pair(true,"")
    }

    //支払い項目スピナーのバリデーションチェック
    fun payListCheck(spPayList:Spinner):Pair<Boolean,String>{
        val selectItem = spPayList.selectedItem.toString()
        if(selectItem == "支払い目的を選択してください"){
            return Pair(false,"未選択です")
        }
        return Pair(true,"")
    }

    //支払金額のバリデーションチェック
    fun payAmountCheck(payAmountEditText: EditText):Pair<Boolean,String>{
        val payAmount = payAmountEditText.text.toString()
        if(!emptyCheck(payAmount)){
            return Pair(false,context.getString(R.string.error_empty))
        }
        if(!numberFormatCheck(payAmount)){
            return Pair(false,context.getString(R.string.error_number_format))
        }
        if(payAmount.toInt() !in 1..1000000){
            return Pair(false,context.getString(R.string.error_range_pay_amount))
        }
        return Pair(true,"")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun payDateCheck(payDateEditText: EditText):Pair<Boolean,String>{
        val payDateStr = payDateEditText.text.toString()
        if(!emptyCheck(payDateStr)){
            return Pair(false,context.getString(R.string.error_empty))
        }
        val MaxDate = LocalDate.now()
        val MinDate = LocalDate.of(1900,1,1)
        val format = DateTimeFormatter.ofPattern("yyy/MM/dd")
        val payDate = LocalDate.parse(payDateStr,format)
        if(!((payDate.isEqual(MaxDate) || payDate.isBefore(MaxDate)) && payDate.isAfter(MinDate))){
            return Pair(false,context.getString(R.string.error_range_pay_date))
        }
        return Pair(true,"")
    }
}
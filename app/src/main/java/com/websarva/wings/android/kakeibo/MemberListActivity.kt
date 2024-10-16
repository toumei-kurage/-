package com.websarva.wings.android.kakeibo

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.websarva.wings.android.kakeibo.helper.DialogHelper
import com.websarva.wings.android.kakeibo.room.member.MemberViewModel
import com.websarva.wings.android.kakeibo.room.member.Person
import com.websarva.wings.android.kakeibo.room.member.PersonAdapter

class MemberListActivity : BaseActivity(R.layout.activity_member_list,R.string.title_member_list) {
    private lateinit var memberViewModel: MemberViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var personAdapter: PersonAdapter
    private lateinit var dialogHelper:DialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)

        setupDrawerAndToolbar()

        // ViewModelのインスタンスを生成し、ユーザーIDを渡す
        memberViewModel = ViewModelProvider(this)[MemberViewModel::class.java]

        dialogHelper = DialogHelper(this)

        //画面部品の取得
        //メンバー追加ボタン
        val buttonMemberAdd = findViewById<FloatingActionButton>(R.id.buttonMemberAdd)
        //リストを取得
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // リストビューに区切り線を入れる
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        buttonMemberAdd.setOnClickListener {
            val intent = Intent(this, MemberAddActivity::class.java)
            startActivity(intent)
            finish()
        }

        // メンバーのリストを監視して更新する
        memberViewModel.getPersons(userID).observe(this) { persons ->
            if (persons != null && persons.isNotEmpty()) {
                personAdapter = PersonAdapter(
                    personList = persons,
                    onUpdateClick = { person ->
                        showUpdateDialog(person)
                    },
                    onDeleteClick = { person ->
                        memberViewModel.deletePerson(person)
                    }
                )
                recyclerView.adapter = personAdapter
            } else {
                // personsがnullまたは空の場合に適切な処理を追加
                // 例えば、"メンバーがいません"と表示するなど
                personAdapter = PersonAdapter(
                    personList = listOf(), // 空のリストを渡す
                    onUpdateClick = { /* 空のリストなので操作なし */ },
                    onDeleteClick = { /* 空のリストなので操作なし */ }
                )
                recyclerView.adapter = personAdapter
            }
        }
    }

    // 更新用のダイアログを表示
    private fun showUpdateDialog(person: Person) {
        val editText = EditText(this)
        editText.setText(person.memberName)

        AlertDialog.Builder(this)
            .setTitle("メンバーの名前を更新")
            .setView(editText)
            .setPositiveButton("更新") { dialog, _ ->
                val newName = editText.text.toString()
                val oldName = person.memberName //退避
                if (newName.isNotEmpty()) {
                    person.memberName = newName
                    memberViewModel.updatePerson(person){result->
                        if (result.success) {
                            dialogHelper.dialogOkOnly("登録成功", result.message)
                        } else {
                            dialogHelper.dialogOkOnly("登録失敗", result.message)
                            person.memberName = oldName
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }
}
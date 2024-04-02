package com.example.sal.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sal.Adapter.DictionaryAdapter;
import com.example.sal.Model.ItemDictionary;
import com.example.sal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DictionaryFragment extends Fragment {
    FirebaseAuth mAuth;
    private DatabaseReference dictionaryRef;
    private RecyclerView recyclerView;
    private DictionaryAdapter adapter;
    private List<ItemDictionary> items = new ArrayList<>();
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dictionaryRef = database.getReference("dictionary");
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView.setAdapter(adapter);
        setHasOptionsMenu(true);

        items = new ArrayList<>();
        adapter = new DictionaryAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);
        DictionaryAdapter adapter = (DictionaryAdapter) recyclerView.getAdapter();

        Button btnAddWord = view.findViewById(R.id.btnAddWord);
        btnAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWordDialog();
            }
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.setTitle("Словарь");
        }

        //Проверка аутентификации пользователя
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            //Проверка роли пользователя
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    // Проверка роли студента
                    if (role != null && role.equals("Студент")) {
                        // Роль пользователя "Студент", скрываем кнопку добавления термина
                        btnAddWord.setVisibility(View.GONE);
                    } else {
                        // Роль пользователя "Преподаватель", отображаем кнопку добавления термина
                        btnAddWord.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обработка ошибки
                }
            });
        }

        adapter.setOnItemClickListener(new DictionaryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ItemDictionary selectedItem = items.get(position);

                // Проверка аутентификации пользователя
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();

                    // Проверка роли пользователя
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                            .child(uid);

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String role = dataSnapshot.child("role").getValue(String.class);

                            // Проверка роли студента
                            if (role != null && role.equals("Студент")) {
                                // Роль пользователя "Студент", не выполняем действия
                                return;
                            } else {
                                // Роль пользователя "Преподаватель", выполняем действия
                                showEditDeleteDialog(position);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        retrieveDataFromDatabase();

        return view;
    }

    private void sortItems(Comparator<ItemDictionary> comparator) {
        Collections.sort(items, comparator);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort1) {
            Comparator<ItemDictionary> comparator = new Comparator<ItemDictionary>() {
                @Override
                public int compare(ItemDictionary item1, ItemDictionary item2) {
                    return item1.getTermin().compareToIgnoreCase(item2.getTermin());
                }
            };
            sortItems(comparator);
            Toast.makeText(requireContext(), "Сортировка от А до Я", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort2) {
            Comparator<ItemDictionary> comparator = new Comparator<ItemDictionary>() {
                @Override
                public int compare(ItemDictionary item1, ItemDictionary item2) {
                    return item2.getTermin().compareToIgnoreCase(item1.getTermin());
                }
            };
            sortItems(comparator);
            Toast.makeText(requireContext(), "Сортировка от Я до А", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dictionary_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList(newText);
                return true;
            }
        });
    }

    private void filteredList(String text) {
        List<ItemDictionary> filteredList = new ArrayList<>();
        boolean wordFound = false;
        String foundTerm = "";

        for (ItemDictionary item : items) {
            if (item.getTermin().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                wordFound = true;
                foundTerm = item.getTermin();
            }
        }

        updateUIWithFilteredList(filteredList);

        if (!text.isEmpty()) {
            if (!wordFound) {
                showWordNotFound();
            }
        }
    }

    private void showWordNotFound() {
        Toast.makeText(requireContext(), "Термин не найден", Toast.LENGTH_SHORT).show();
    }

    private void updateUIWithFilteredList(List<ItemDictionary> filteredList) {
        // Например, обновляем RecyclerView с использованием отфильтрованного списка
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);

        // Если у вас есть лайаут-менеджер RecyclerView, обновите его
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Создайте новый адаптер и установите его в RecyclerView
        DictionaryAdapter updatedAdapter = new DictionaryAdapter(requireContext(), filteredList);
        recyclerView.setAdapter(updatedAdapter);
    }
    private void setItems(List<ItemDictionary> items) {
        this.items = items;
    }




    private void retrieveDataFromDatabase() {
        dictionaryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                ItemDictionary newItem = dataSnapshot.getValue(ItemDictionary.class);
                if (newItem != null) {
                    newItem.setId(dataSnapshot.getKey());
                    items.add(newItem);
                    adapter.notifyDataSetChanged();
                    setItems(items);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                ItemDictionary updatedItem = dataSnapshot.getValue(ItemDictionary.class);
                if (updatedItem != null) {
                    updatedItem.setId(dataSnapshot.getKey());
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getId().equals(updatedItem.getId())) {
                            items.set(i, updatedItem);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String removedKey = dataSnapshot.getKey();

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getId().equals(removedKey)) {
                        items.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showAddWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Добавить новый термин");

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogLayout = inflater.inflate(R.layout.dialog_add_word, null);
        builder.setView(dialogLayout);

        final EditText addTermin = dialogLayout.findViewById(R.id.AddTermin);
        final EditText addPonyatie = dialogLayout.findViewById(R.id.AddPonyatie);

        builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String termin = addTermin.getText().toString();
                String ponyatie = addPonyatie.getText().toString();

                if (!TextUtils.isEmpty(termin) && !TextUtils.isEmpty(ponyatie)) {
                    ItemDictionary newItem = new ItemDictionary(termin, ponyatie);

                    String key = dictionaryRef.push().getKey();
                    newItem.setId(key);
                    dictionaryRef.child(key).setValue(newItem);
                }
            }
        });

        builder.setNegativeButton("Отмена", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showEditDeleteDialog(final int position) {
        ItemDictionary item = items.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите действие");
        builder.setItems(new String[]{"Редактировать термин", "Удалить термин"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        showEditWordDialog(position);
                        break;
                    case 1:
                        showDeleteWordDialog(position);
                        break;
                }
            }
        });

        builder.create().show();
    }

    private void showEditWordDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Редактировать термин");

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogLayout = inflater.inflate(R.layout.dialog_edit_word, null);
        builder.setView(dialogLayout);

        final EditText EdTermin = dialogLayout.findViewById(R.id.EdTermin);
        final EditText EdPonyatie = dialogLayout.findViewById(R.id.EdPonyatie);

        ItemDictionary item = items.get(position);
        EdTermin.setText(item.getTermin());
        EdPonyatie.setText(item.getPonyatie());

        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String termin = EdTermin.getText().toString();
                String ponyatie = EdPonyatie.getText().toString();

                if (!TextUtils.isEmpty(termin) && !TextUtils.isEmpty(ponyatie)) {
                    ItemDictionary updatedItem = items.get(position);
                    updatedItem.setTermin(termin);
                    updatedItem.setPonyatie(ponyatie);
                    dictionaryRef.child(updatedItem.getId()).setValue(updatedItem);
                }
            }
        });

        builder.setNegativeButton("Отмена", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteWordDialog(final int position) {
        ItemDictionary item = items.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Удалить термин");
        builder.setMessage("Вы действительно хотите удалить термин \"" + item.getTermin() + "\"?");

        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ItemDictionary deletedItem = items.get(position);
                dictionaryRef.child(deletedItem.getId()).removeValue();
            }
        });

        builder.setNegativeButton("Отмена", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentMemberEditBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validateDateOfBirth
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validatePhoneNumber
import vn.edu.hust.ttkien0311.smartlockdoor.network.Member
import vn.edu.hust.ttkien0311.smartlockdoor.network.MemberDto
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class MemberEditFragment : Fragment() {
    private lateinit var binding: FragmentMemberEditBinding
    private val viewModel: MemberViewModel by activityViewModels()
    private val args: MemberEditFragmentArgs by navArgs()

    private lateinit var imageView: ShapeableImageView
    private lateinit var name: EditText
    private lateinit var dateOfBirth: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var updateBtn: Button

    private lateinit var errorName: TextView
    private lateinit var errorDateOfBirth: TextView
    private lateinit var errorPhone: TextView

    private var imageChecked = false
    private var nameChecked = false
    private var dateChecked = false
    private var phoneChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_member_edit, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        name = binding.editMemberName
        errorName = binding.errorName

        dateOfBirth = binding.editDateOfBirth
        errorDateOfBirth = binding.errorDateOfBirth

        phoneNumber = binding.editPhoneNumber
        errorPhone = binding.errorPhone

        updateBtn = binding.updateButton
        if (args.state == State.ADD.toString()) {
            updateBtn.text = resources.getString(R.string.save)
            binding.toolbar.title.text = "Thêm mới"
        } else {
            updateBtn.text = resources.getString(R.string.update)
            binding.toolbar.title.text = "Cập nhập"
        }
        imageView = binding.memberImage
        if (viewModel.member.value == null) {
            imageView.setBackgroundColor(resources.getColor(R.color.white, null))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.selectImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                errorName.visibility = View.INVISIBLE
                followInput()
            }
        })

        dateOfBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                errorDateOfBirth.visibility = View.INVISIBLE
                followInput()
            }
        })

        phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                errorPhone.visibility = View.INVISIBLE
                followInput()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data!!
                binding.memberImage.setImageURI(uri)
                binding.updateButton.isClickable = true
                binding.updateButton.setBackgroundColor(
                    resources.getColor(
                        R.color.my_light_primary,
                        null
                    )
                )
                binding.updateButton.setOnClickListener { clickUpdateBtn() }
            }

            ImagePicker.RESULT_ERROR -> {
                Log.d("SLD", "Image picker: ${ImagePicker.getError(data)}")
                Toast.makeText(
                    requireActivity(),
                    "Có lỗi xảy ra, vui lòng thử lại",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    fun followInput() {
        if (!name.text.isNullOrEmpty() && name.text.toString()
                .trim() == viewModel.member.value?.memberName && !dateOfBirth.text.isNullOrEmpty() && dateOfBirth.text.toString()
                .trim() == viewModel.member.value?.dateOfBirth && !phoneNumber.text.isNullOrEmpty() && phoneNumber.text.toString()
                .trim() == viewModel.member.value?.phoneNumber
        ) {
            updateBtn.isClickable = false
            updateBtn.setOnClickListener(null)
            updateBtn.setBackgroundColor(
                resources.getColor(
                    R.color.my_light_primary_disable,
                    null
                )
            )
        } else {
            updateBtn.isClickable = true
            updateBtn.setBackgroundColor(resources.getColor(R.color.my_light_primary, null))
            updateBtn.setOnClickListener { clickUpdateBtn() }
        }
    }

     fun clickUpdateBtn() {
        if (binding.memberImage.drawable != null) {
            imageChecked = true
        } else {
            imageChecked = false
            AlertDialog.Builder(requireActivity())
                .setMessage("Vui lòng chọn ảnh")
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        if (name.text.isNullOrEmpty()) {
            nameChecked = false
            errorName.text = "Vui lòng nhập tên"
            errorName.visibility = View.VISIBLE
        } else if (name.text.toString().trim().length > 50) {
            nameChecked = false
            errorName.text = "Tên không được dài quá 50 ký tự"
            errorName.visibility = View.VISIBLE
        } else {
            nameChecked = true
            errorName.visibility = View.INVISIBLE
        }

        dateChecked = validateDateOfBirth(dateOfBirth.text.toString().trim())
        if (dateChecked) {
            errorDateOfBirth.visibility = View.INVISIBLE
        } else {
            errorDateOfBirth.text = "Ngày sinh không hợp lệ"
            errorDateOfBirth.visibility = View.VISIBLE
        }

        phoneChecked = validatePhoneNumber(phoneNumber.text.toString().trim())
        if (phoneChecked) {
            errorPhone.visibility = View.INVISIBLE
        } else {
            errorPhone.text = "Số điện thoại không hợp lệ"
            errorPhone.visibility = View.VISIBLE
        }

        if (imageChecked && nameChecked && dateChecked && phoneChecked) {
            val imgBitmap = imageView.drawable.toBitmap()
            val imgBase64 = Helper.bitmapToBase64(imgBitmap)

            val inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            var inputDate: String? = dateOfBirth.text.toString().trim()
            inputDate = try {
                LocalDate.parse(inputDate, inputFormat)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            } catch (e: DateTimeParseException) {
                null
            }

            var phone: String? = null
            if (phoneNumber.text.toString().trim().isNotEmpty()) {
                phone = phoneNumber.text.toString().trim()
            }

            lifecycleScope.launch {
                try {
                    showLoading(requireActivity())

                    val memberDto = MemberDto(
                        name.text.toString().trim(),
                        args.deviceId,
                        imgBase64,
                        inputDate,
                        phone
                    )

                    val res = if (args.state == State.EDIT.toString()) {
                        ServerApi(requireActivity()).retrofitService.changeMemberInfo(
                            viewModel.member.value!!.memberId,
                            memberDto
                        )
                    } else {
                        ServerApi(requireActivity()).retrofitService.createMember(memberDto)
                    }

                    if (res != null) {
                        hideLoading()
                        res.dateOfBirth = formatDateTime(res.dateOfBirth, "dd/MM/yyyy")
                        res.createdDate = formatDateTime(res.createdDate, "HH:mm - dd/MM/yyyy")
                        viewModel.onMemberRowClicked(res)

                        AlertDialog.Builder(requireActivity())
                            .setMessage("Thành công!")
                            .setPositiveButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                                findNavController().popBackStack()
                            }
                            .create()
                            .show()
                    } else {
                        hideLoading()
                        AlertDialog.Builder(requireActivity())
                            .setMessage("Thất bại.\nVui lòng thử lại sau")
                            .setPositiveButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                } catch (ex: Exception) {
                    hideLoading()
                    handleException(ex, requireActivity())
                }
            }
        }
    }
}


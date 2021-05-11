package com.example.cleanarchitechture.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cleanarchitechture.R
import com.example.cleanarchitechture.domain.entity.Person
import com.example.cleanarchitechture.presentation.service.AddPersonService
import com.example.cleanarchitechture.Constants
import com.example.cleanarchitechture.presentation.adapter.ItemClickListener
import com.example.cleanarchitechture.presentation.adapter.PersonAdapter
import com.example.cleanarchitechture.presentation.service.GetPersonsService
import com.example.cleanarchitechture.presentation.viewmodel.MainViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MainFragment : Fragment(), ItemClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var nameInput: EditText
    private lateinit var ratingInput: EditText
    private lateinit var addPersonBtn: Button
    private lateinit var personsList: RecyclerView
    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager
    private var allPersonsAdapter = PersonAdapter()
    private var sensorGravity: Sensor? = null

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            ratingInput.setText("${location.latitude.toInt() * 100 + location.longitude.toInt()}")
        }

        @SuppressLint("MissingPermission")
        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            val location = locationManager.getLastKnownLocation(provider)
        }

    }
    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                nameInput.setText("${event.values[0]} ${event.values[1]} ${event.values[2]}")
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

    }

    private lateinit var refresher: SwipeRefreshLayout

    private val disposable: CompositeDisposable = CompositeDisposable()

    private var addPersonService: AddPersonService? = null
    private var boundToAddPersonService: Boolean = false
    private var newPersonData: Pair<String, Float>? = null

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AddPersonService.LocalBinder
            addPersonService = binder.getService()
            boundToAddPersonService = true
            newPersonData?.let {
                startAddPersonProcess(it.first, it.second)
            }
            newPersonData = null
            Log.d("AddPerson", "onServiceConnected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            addPersonService = null
            boundToAddPersonService = false
            Log.d("AddPerson", "onServiceDisconnected")
        }
    }

    private val batteryLeverBroadcastReceiver: BatteryLeverBroadcastReceiver by lazy {
        BatteryLeverBroadcastReceiver()
    }
    private val personAddedReceiver = PersonAddedReceiver()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        requireContext().registerReceiver(
                personAddedReceiver,
                IntentFilter(Constants.PERSON_ADDED_BROADCAST)
        )
        requireContext().registerReceiver(
                batteryLeverBroadcastReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        sensorManager.registerListener(
                accelerometerListener,
                sensorGravity,
                SensorManager.SENSOR_DELAY_NORMAL
        )
        if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.FINE_LOCATION_REQUEST_CODE)
        } else {
            checkLocation()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.FINE_LOCATION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty()) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkLocation()
                }
                return
            }
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(personAddedReceiver)
        requireActivity().unregisterReceiver(batteryLeverBroadcastReceiver)
        sensorManager.unregisterListener(accelerometerListener)
        locationManager.removeUpdates(locationListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        nameInput.doAfterTextChanged {
            viewModel.name = it.toString()
        }
        ratingInput.doAfterTextChanged {
            viewModel.rating = it.toString()
        }
        refresher.setOnRefreshListener {
            viewModel.updatePersons()
        }


        val observable = Observable.create<Unit> { emitter ->
            addPersonBtn.setOnClickListener {
                emitter.onNext(Unit)
            }
        }
        val subscribe = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.addPerson() }
        disposable.add(subscribe)

        viewModel.getPersons().observe(viewLifecycleOwner, {
            allPersonsAdapter.setData(it)
            refresher.isRefreshing = false
        })

        viewModel.getError().observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

        viewModel.getPersonDataReady().observe(viewLifecycleOwner, {
            startAddPersonProcess(it.first, it.second)
        })

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        sensors.forEach { sensor ->
            val sensorInformation =
                    "name = ${sensor.name}, type = ${sensor.type}\nvendor = ${sensor.vendor}" +
                            " ,version = ${sensor.version}\nmax = ${sensor.maximumRange} , power = ${sensor.power}" +
                            ", resolution = ${sensor.resolution}\n--------------------------------------\n"
            Log.d("Sensor", sensorInformation)
        }
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun startAddPersonProcess(name: String, rating: Float) {
        if (boundToAddPersonService) {
            addPersonService?.startAddPersonProcess(name, rating)
        } else {
            val addPersonServiceIntent =
                    Intent(requireContext(), AddPersonService::class.java).apply {
                        this.putExtra(Constants.NAME, name)
                        this.putExtra(Constants.RATING, rating)
                    }
            requireActivity().bindService(
                    addPersonServiceIntent,
                    connection,
                    Context.BIND_AUTO_CREATE
            )
            newPersonData = Pair(name, rating)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInput = view.findViewById(R.id.name_input)
        ratingInput = view.findViewById(R.id.rating_input)
        addPersonBtn = view.findViewById(R.id.add_person_btn)
        personsList = view.findViewById(R.id.persons_list)

        personsList.layoutManager = LinearLayoutManager(requireContext())
        personsList.adapter = allPersonsAdapter
        allPersonsAdapter.setListener(this)

        refresher = view.findViewById(R.id.refresher)
    }

    override fun onItemClick(person: Person) {
        viewModel.onPersonSelected(person)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        allPersonsAdapter.setListener(null)
    }

    @SuppressLint("MissingPermission")
    fun checkLocation() {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                5F,
                locationListener
        )
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000L,
                5F,
                locationListener
        )
    }


    inner class BatteryLeverBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level: Int = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
//            ratingInput.setText(level.toString())
        }
    }

    class PersonAddedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("AddPerson", "PersonAddedReceiver onReceive")
            val actionRequired = intent?.getBooleanExtra(Constants.ACTION_REQUIRED, false) ?: false
            if (actionRequired && context != null) {
                Intent(context, GetPersonsService::class.java).also {
                    it.putExtra(Constants.ACTION_REQUIRED, true)

                    JobIntentService.enqueueWork(
                            context,
                            GetPersonsService::class.java,
                            Constants.GET_PERSONS_JOB_ID,
                            it
                    )
                }
            }
        }
    }
}

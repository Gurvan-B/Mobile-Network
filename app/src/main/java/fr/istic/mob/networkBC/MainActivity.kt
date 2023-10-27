package fr.istic.mob.networkBC

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import fr.istic.mob.networkBC.Models.Device
import fr.istic.mob.networkBC.Models.Graph
import fr.istic.mob.networkBC.Models.Link
import fr.istic.mob.networkBC.constants.ModesStates
import fr.istic.mob.networkBC.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var state: ModesStates = ModesStates.NOMODE
    private lateinit var binding: ActivityMainBinding
    private lateinit var image: ImageView
    private var selectedDevice: Device? = null
    private var selectedLink: Link? = null
    private var temporaryLink: Link? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Suppression du titre et de l'action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        supportActionBar?.hide() // hide the title bar

        setContentView(R.layout.activity_main)

        // Gest
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner: Spinner = findViewById(R.id.modes_Spinner)

        val arrayAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.modes,
            android.R.layout.simple_spinner_item
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = this

        val myDrawing = DrawableGraph()

        image = findViewById(R.id.points)
        image.setImageDrawable(myDrawing)




        image.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onActionDown(v, event)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        onActionMove(v, event)
                    }
                    MotionEvent.ACTION_UP -> {
                        onActionUp(v, event)
                    }
                }

                image.invalidate()

                return true
            }
        })




        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            Graph.mainGraph.devices = ArrayList()
            Graph.mainGraph.links = ArrayList()
            image.invalidate()
        }

    }

    private fun onActionDown(v: View?, event: MotionEvent) {
        val x = event.x
        val y = event.y
        when (this.state) {
            ModesStates.AJOUTEROBJET -> {
                val device = Device(x, y)
                Graph.mainGraph.addDevice(device)

                printDialogCreateDevice(device)

            }
            ModesStates.AJOUTERCONNECTION -> {
                val device1 = findNearDevice(x, y)
                if (device1 != null) {
                    //selectedLink = Link(device1,null)
                    val link = Link(device1)
                    temporaryLink = link
                    Graph.mainGraph.addLink(link)
                }
            }
            ModesStates.DEPLACER -> {
                var obj = findNearObject(x,y)
                if (obj is Device) {
                    selectedDevice = obj
                } else if (obj is Link) {
                    selectedLink = obj
                } else {
                    selectedDevice = null
                    selectedLink = null
                }
            }
            ModesStates.MODIFIER -> {
                val obj = findNearObject(x,y)
                if (obj is Device) {

                    val popupMenu = PopupMenu(this,image)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.menu_supprimer -> {
                                Graph.mainGraph.removeDevice(obj)
                                image.invalidate()
                                true
                            }
                            R.id.menu_renommer -> {
                                printDialogRenameDevice(obj)
                                image.invalidate()
                                true
                            }
                            R.id.menu_changer_couleur -> {
                                showColorPickerMenu(obj)
                                image.invalidate()
                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.inflate(R.menu.device_menu)
                    popupMenu.show()

                } else if (obj is Link) {


                    val popupMenu = PopupMenu(this,image)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.menu_supprimer_lien -> {
                                Graph.mainGraph.removeLink(obj)
                                image.invalidate()
                                true
                            }
                            R.id.menu_renommer_lien -> {
                                printDialogRenameLink(obj)
                                image.invalidate()
                                true
                            }
                            R.id.menu_epaissir_lien -> {
                                printDialogNewLinkWidth(obj)
                                image.invalidate()
                                true
                            }
                            R.id.menu_changer_couleur_lien -> {
                                showColorPickerMenu(obj)

                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.inflate(R.menu.link_menu)
                    popupMenu.show()

                }
            }
            ModesStates.NOMODE -> {

            }
        }

    }

    private fun onActionMove(v: View?, event: MotionEvent) {
        val x = event.x
        val y = event.y
        when (this.state) {
            ModesStates.AJOUTEROBJET -> {

            }
            ModesStates.AJOUTERCONNECTION -> {
                temporaryLink?.endPosX = x
                temporaryLink?.endPosY = y
            }
            ModesStates.DEPLACER -> {
                if (x>0F && x<image.width && y>0F && y < image.height ) {
                    selectedDevice?.x = x
                    selectedDevice?.y = y

                    selectedLink?.middleX = x
                    selectedLink?.middleY = y
                }
            }
            ModesStates.MODIFIER -> {

            }
            ModesStates.NOMODE -> {

            }
        }
    }

    private fun onActionUp(v: View?, event: MotionEvent) {
        val x = event.x
        val y = event.y
        when (this.state) {
            ModesStates.AJOUTEROBJET -> {

            }
            ModesStates.AJOUTERCONNECTION -> {
                val device = findNearDevice(x, y)
                if (device != null && device != temporaryLink?.device1 && device.link == null ) {
                    temporaryLink?.let {

                        it.device2 = device
                        it.device1.link = it
                        it.device2.link = it
                        it.middleX = (it.device1.x + it.device2.x) / 2
                        it.middleY = (it.device1.y + it.device2.y) / 2

                        printDialogCreateLink(it)

                    }
                } else {
                    temporaryLink?.let { Graph.mainGraph.removeLink(it) }
                }
                temporaryLink = null


            }
            ModesStates.DEPLACER -> {

            }
            ModesStates.MODIFIER -> {

            }
            ModesStates.NOMODE -> {

            }
        }
    }

    private fun showColorPickerMenu(obj:Any) {
        val popupMenu = PopupMenu(this,image)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_rouge -> {
                    setObjetColor(obj,Color.RED)
                    true
                }
                R.id.menu_vert -> {
                    setObjetColor(obj,Color.GREEN)
                    true
                }
                R.id.menu_jaune -> {
                    setObjetColor(obj,Color.YELLOW)
                    true
                }
                R.id.menu_cyan -> {
                    setObjetColor(obj,Color.CYAN)
                    true
                }
                R.id.menu_magenta -> {
                    setObjetColor(obj,Color.MAGENTA)
                    true
                }
                R.id.menu_noir -> {
                    setObjetColor(obj,Color.BLACK)
                    true
                }
                else -> false
            }
        }

        popupMenu.inflate(R.menu.color_pick)
        popupMenu.show()
    }

    private fun setObjetColor (obj:Any, color:Int) {
        if (obj is Device) obj.setColor(color)
        if (obj is Link) obj.setColor(color)
        image.invalidate()
    }

    private fun printDialogCreateLink(link:Link) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.new_link)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(R.string.create,
            DialogInterface.OnClickListener { dialog, which ->
                link.nom = input.text.toString()
                image.invalidate()
            })
        builder.setNegativeButton(
            R.string.undo,
            DialogInterface.OnClickListener { dialog, which ->
                Graph.mainGraph.removeLink(link)
                image.invalidate()
                dialog.cancel()
            })

        builder.show()
    }

    private fun printDialogCreateDevice(device:Device) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.new_device)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(R.string.create
        ) { dialog, which ->
            device.nom = input.text.toString()
            image.invalidate()
        }
        builder.setNegativeButton(
            R.string.undo
        ) { dialog, which ->
            Graph.mainGraph.removeDevice(device)
            image.invalidate()
            dialog.cancel()
        }

        builder.show()
    }

    private fun printDialogRenameDevice(device:Device) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.new_device)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(R.string.create
        ) { dialog, which ->
            device.nom = input.text.toString()
            image.invalidate()
        }

        builder.show()
    }

    private fun printDialogRenameLink(link:Link) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.new_device)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(R.string.create
        ) { dialog, which ->
            link.nom = input.text.toString()
            image.invalidate()
        }

        builder.show()
    }

    private fun printDialogNewLinkWidth(link:Link) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.new_link)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setPositiveButton(R.string.create
        ) { dialog, which ->
            link.strokeWidth = input.text.toString().toFloat()
            image.invalidate()
        }

        builder.show()
    }

    private fun findNearDevice(x: Float, y: Float): Device? {
        for (device in Graph.mainGraph.devices) {
            if (device.x > x - 50F - 10F && device.x < x + 50F + 10F && device.y > y - 50F - 10F && device.y < y + 50F + 10F) {
                return device
            }
        }
        return null
    }

    private fun findNearObject(x: Float, y: Float): Any? {
        val device = findNearDevice(x,y)
        if (device != null ) return device

        for (link in Graph.mainGraph.links) {
            if (link.middleX > x - 50F - 10F && link.middleX < x + 50F + 10F && link.middleY > y - 50F - 10F && link.middleY < y + 50F + 10F) {
                return link
            }
        }
        return null

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        this.state = ModesStates.values()[p2]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}


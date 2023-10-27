package fr.istic.mob.networkBC.Models

class Graph {
    var devices: ArrayList<Device> = ArrayList()
    var links: ArrayList<Link> = ArrayList()

    companion object Graph {
        val mainGraph = Graph()
    }


    fun addDevice(device:Device) {
        devices.add(device)
    }

    fun removeDevice(device:Device) {
        devices.remove(device)
        device.link?.let {
            removeLink(it)
        }
    }

    fun addLink(link:Link) {
        links.add(link)
    }

    fun removeLink(link:Link) {
        links.remove(link)
    }

}
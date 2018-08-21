/*
* Author: N Vass
*
* Json Switch Device Handler
*/


preferences {
    
	section("Internal Access"){
		input "internal_ip", "text", title: "Internal IP", required: false
		input "internal_port", "text", title: "Internal Port (if not 80)", required: false
		input "json_on", "text", title: "On Json", required: false
		input "json_off", "text", title: "Off Jason", required: false
	}
}

metadata {
	definition (name: "Json Switch v4", namespace: "NVass", author: "N Vass") {
		capability "Actuator"
		capability "Switch"		
	}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles {
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "on"
				state "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "off"
		}
		standardTile("offButton", "device.button", width: 1, height: 1, canChangeIcon: true) {
			state "default", label: 'Force Off', action: "switch.off", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
		standardTile("onButton", "device.switch", width: 1, height: 1, canChangeIcon: true) {
			state "default", label: 'Force On', action: "switch.on", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}
		main "button"
			details (["button","onButton","offButton"])
	}
}

def parse(String description) {
	log.debug(description)
}

def on() {
	if (json_on){
		def port
			if (internal_port){
				port = "${internal_port}"
			} else {
				port = 80
			}

		def result = new physicalgraph.device.HubAction(
				method: "POST",
				body: "${json_on}",
				headers: [
				HOST: "${internal_ip}:${port}"
				]
				)
			sendHubCommand(result)
			sendEvent(name: "switch", value: "on") 
			log.debug "Executing ON" 
			log.debug result
	}
}

def off() {
	if (json_off){
		def port
			if (internal_port){
				port = "${internal_port}"
			} else {
				port = 80
			}

		def result = new physicalgraph.device.HubAction(
				method: "POST",
				body: "${json_off}",
				headers: [
				HOST: "${internal_ip}:${port}"
				]
				)

			sendHubCommand(result)
			sendEvent(name: "switch", value: "off")
			log.debug "Executing OFF" 
			log.debug result
	}
}

/*
* Author: N Vass
*
* Json Switch Device Handler
*/

include 'asynchttp_v1'

preferences {
    
	section("Internal Access"){
		input "internal_ip", "text", title: "Internal IP", required: false
		input "internal_port", "text", title: "Internal Port (if not 80)", required: false
		input "json_on", "text", title: "On Json", required: false
		input "json_off", "text", title: "Off Json", required: false
	}
}

metadata {
	definition (name: "Json Switch", namespace: "NVass", author: "N Vass") {
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
		standardTile("offButton", "device.switch", width: 1, height: 1, canChangeIcon: true) {
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
		def port
			if (internal_port){
				port = "${internal_port}"
			} else {
				port = 80
			}

		def commands =  ["relay1":"on", "relay2":"on", "relay3":"on", "relay4":"on"]
	
		def params =    [
				uri: "${internal_ip}:${port}",
				headers: ["Content-Type": "application/json", "Accept": "application/json"],
				body: commands
				]
			
		//sendHubCommand(result)
		sendEvent(name: "switch", value: "on") 
		log.debug "Executing ON" 
		log.debug params
		
	try {
		asynchttp_v1.post('postResponseHandler', params)
	} catch(e) {
		log.error e
	}
}

def off() {
		def port
			if (internal_port){
				port = "${internal_port}"
			} else {
				port = 80
			}

		def commands =  ["relay1":"on", "relay2":"on", "relay3":"on", "relay4":"on"]
	
		def params =    [
				uri: "${internal_ip}:${port}",
				headers: ["Content-Type": "application/json", "Accept": "application/json"],
				body: commands
				]

			//sendHubCommand(result)
			sendEvent(name: "switch", value: "off")
			log.debug "Executing OFF" 
			log.debug params
		
		try {
		asynchttp_v1.post('postResponseHandler', params)
	} catch(e) {
		log.error e
	}
}

def postResponseHandler(response, data) {

    if(response.getStatus() == 200 || response.getStatus() == 207) {
	log.info "POST response received from the device."
    } else {
        log.error "POST Error: ${response.getErrorData()}"
    }
}

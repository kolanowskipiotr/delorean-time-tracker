# How to install: Add to ~/.zshrc two below lines
## DeLorean Time Tracker
#. {PATH_TO_THIS_FILE}

function dtt {
	cd /home/pkolanow/personal-workspace/delorean-time-tracker-RELEASES/delorean-time-tracker-1.2.1
    java -jar delorean-time-tracker-1.2.1.jar
}

# This makefile allows to compile automatically java files and produce a jar

#commands
JAVAC = javac
JAR= jar
JAVA=java


MANIFEST= META-INF/MANIFEST.MF

# Main target and filename of the executable
JARFILE = main.jar
OUT_DIR=out

SRC_DIR=src
BUILD_DIR = build
PCAP=res/dhcp.pcap

# Recursive Wildcard function
rwildcard=$(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2)$(filter $(subst *,%,$2),$d))
# Remove duplicate function
uniq = $(if $1,$(firstword $1) $(call uniq,$(filter-out $(firstword $1),$1))) 

# List of all the .java source files to compile
SRC = $(call rwildcard,$(SRC_DIR),*.java)

# List of all the .class object files to produce
OBJ = $(patsubst $(SRC_DIR)/%.java,$(BUILD_DIR)/%.class,$(SRC))
OBJ_NAME = $(patsubst $(BUILD_DIR)/%.class,%,$(OBJ))
OBJ_CLASS = $(patsubst $(BUILD_DIR)/%.class,%.class,$(OBJ))
OBJ_DIRS = $(call uniq, $(dir $(OBJ)))

# path of jar
OUT=$(OUT_DIR)/$(JARFILE)


all: $(OBJ_DIRS)
	@$(MAKE) run -s

$(OBJ_DIRS) $(OUT_DIR):
	@echo "Creating folder $@..."
	@mkdir -p $@	


$(BUILD_DIR)/%.class: $(SRC_DIR)/%.java
	@echo "Compiling $<..."
	@$(JAVAC) $< -d $(BUILD_DIR) -sourcepath $(SRC_DIR)

$(OUT): $(OBJ) $(OUT_DIR)
	@echo "Creating jar $@..."
	@cd $(BUILD_DIR) && $(JAR) cvfm ../$(OUT) ../$(MANIFEST) $(OBJ_CLASS)

clean:
	@echo "Cleaning Build"
	@rm -rf $(BUILD_DIR) $(OUT_DIR)

runjar: $(OUT)
	@echo "Running $(OUT)..."
	@$(JAVA) -jar $(OUT_DIR)/$(JARFILE)

run: $(OBJ)
	@$(JAVA) -cp $(BUILD_DIR) Main $(PCAP)
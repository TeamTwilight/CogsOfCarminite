
var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

// noinspection JSUnusedGlobalSymbols
function initializeCoreMod() {
    return {
        'smart_observer': {
            'target': {
                'type': 'METHOD',
                'class': 'com.simibubi.create.content.redstone.smartObserver.SmartObserverBlockEntity',
                'methodName': 'tick',
                'methodDesc': '()V'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                instructions.insert(
                    ASM.findFirstMethodCall(
                        methodNode,
                        ASM.MethodType.VIRTUAL,
                        'com/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour',
                        'test',
                        '(Lnet/minecraft/world/item/ItemStack;)Z'
                    ),
                    ASM.listOf(
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            'com/simibubi/create/content/redstone/smartObserver/SmartObserverBlockEntity',
                            'filtering',
                            'Lcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;'
                        ),
                        new VarInsnNode(Opcodes.ALOAD, 0),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            'com/cogsofcarminite/ASMHooks',
                            'filterAsBlock',
                            '(ZLcom/simibubi/create/foundation/blockEntity/behaviour/filtering/FilteringBehaviour;Lnet/minecraft/world/level/block/entity/BlockEntity;)Z',
                            false
                        )
                    )
                );
                return methodNode;
            }
        }
    }
}
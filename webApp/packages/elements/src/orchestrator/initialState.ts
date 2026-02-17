import type { OrchestratorCoreState, MethodUIState, MoleculePolicy } from './state';
import type { PaymentMethod } from '../shared/types';

const allMethods: PaymentMethod[] = ['applePay', 'googlePay',
    //'card', 'cash', 'bankTransfer', 'bnpl', 'payByBank'
];

const mk = (): MethodUIState => ({
    mounted: false,
    ready: false,
    active: false,
    enabled: true,
    blocked: false,
    visible: false,
});

export const createInitialState = (policy: MoleculePolicy): OrchestratorCoreState => ({
    viewState: 'editing',
    activeMethod: undefined,
    policy,
    methods: allMethods.reduce((acc, m) => {
        acc[m] = mk();
        return acc;
    }, {} as OrchestratorCoreState['methods']),
});
